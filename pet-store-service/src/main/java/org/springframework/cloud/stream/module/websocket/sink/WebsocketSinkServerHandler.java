/*
  * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:

 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.stream.module.websocket.sink;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.lnu.service.common.channels.Auth;
import se.lnu.service.common.message.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * Handles handshakes and messages. Based on the Netty <a href="http://bit.ly/1jVBj5T">websocket examples</a>.
 *
 * @author Netty
 * @author Oliver Moser
 * @author Ioseff Griffith (updated
 */
class WebsocketSinkServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Log logger = LogFactory.getLog(WebsocketSinkServerHandler.class);

	private final boolean traceEnabled;

	private final TraceRepository websocketTraceRepository;

	private final WebsocketSinkProperties properties;

	private WebSocketServerHandshaker handshaker;
	
	private UserController userController;
	
	private InventoryController inventoryController;

	public WebsocketSinkServerHandler(TraceRepository websocketTraceRepository,
									  WebsocketSinkProperties properties,
									  boolean traceEnabled,
									  UserController userController,
									  InventoryController inventoryController) {

		this.websocketTraceRepository = websocketTraceRepository;
		this.properties = properties;
		this.traceEnabled = traceEnabled;
		this.userController = userController;
		this.inventoryController = inventoryController;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		// Handle a bad request.
		if (!req.getDecoderResult().isSuccess()) {
			logger.warn(String.format("Bad request: %s", req.getUri()));
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
			return;
		}

		// Allow only GET methods.
		if (req.getMethod() != GET) {
			logger.warn(String.format("Unsupported HTTP method: %s", req.getMethod()));
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}

		// enable subclasses to do additional processing
		if (!additionalHttpRequestHandler(ctx, req)) {
			return;
		}

		// Handshake
		WebSocketServerHandshakerFactory wsFactory
			= new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);

		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
			WebsocketSinkServer.channels.add(ctx.channel());
		}
	}


	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			addTraceForFrame(frame, "close");
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			addTraceForFrame(frame, "ping");
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
				.getName()));
		}

		// todo [om] think about BinaryWebsocketFrame

		handleTextWebSocketFrameInternal((TextWebSocketFrame) frame, ctx);
	}

	private boolean additionalHttpRequestHandler(ChannelHandlerContext ctx, FullHttpRequest req) {
		// implement other HTTP request logic
		return true; // continue processing
	}

	private void handleTextWebSocketFrameInternal(TextWebSocketFrame frame, ChannelHandlerContext ctx) {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s received %s", ctx.channel(), frame.text()));
		}

		addTraceForFrame(frame, "text");
		
		JsonObject jsonObject = new JsonParser().parse(frame.text()).getAsJsonObject();
			
		String reqType = jsonObject.get("type").getAsString();
		
		// Some requests don't require auth
		switch (reqType) {
		case "requestInventory":
			inventoryController.requestInventory(ctx.channel().id().toString());
			return;
		case "register":
			jsonObject.addProperty("channelId", ctx.channel().id().toString());
			userController.registerUser(jsonObject);
			return;
		case "login":
			jsonObject.addProperty("channelId", ctx.channel().id().toString());
			userController.loginUser(jsonObject);
			return;
		}
		
		// Others do
		if (!jsonObject.has("token")) {
			ctx.channel().write(new TextWebSocketFrame("{ \"error\": \"Not logged in.\" }"));
			return;
		}
		String token = jsonObject.get("token").getAsString();
		
		//if (!jsonObject.has("token")) {
		// TODO if token not valid
		//	ctx.channel().write(new TextWebSocketFrame("{ \"error\": \"Invalid token.\" }"));
		//	return;
		//}
		
		String email = token; //TODO get from token
		jsonObject.addProperty("userEmail", email);
		Set<String> emails;
		if (!WebsocketSinkServer.emailsToChannel.containsKey(email)) {
			emails = Collections.synchronizedSet(new HashSet<String>());
			WebsocketSinkServer.emailsToChannel.put(email, emails);
		}
		else {
			emails = WebsocketSinkServer.emailsToChannel.get(email);
		}
		emails.add(ctx.channel().id().toString());

		switch (reqType) {
		case "addToCart":
			inventoryController.addToCart(jsonObject);
			break;
		case "requestCart":
			inventoryController.requestCart(email);
			break;
		case "getUser":
			userController.getUser(jsonObject);
			break;
		case "updateUser": // TODO FIX
			userController.changeUser(jsonObject);
			break;
		case "removeFromCart":
			inventoryController.removeFromCart(jsonObject);
			break;
		default:
			throw new IllegalArgumentException("Invalid type of request");
		}
		//User user = new User();
		//user.setFirstName(frame.text());
		//userController.registerUser(user);
		//auth.output().send(MessageBuilder.withPayload(user).build());
		//ctx.channel().write(new TextWebSocketFrame("Echo: " + frame.text()));
	}

	// add trace information for received frame
	private void addTraceForFrame(WebSocketFrame frame, String type) {
		Map<String, Object> trace = new LinkedHashMap<String, Object>();
		trace.put("type", type);
		trace.put("direction", "in");
		if (frame instanceof TextWebSocketFrame) {
			trace.put("payload", ((TextWebSocketFrame) frame).text());
		}

		if (traceEnabled) {
			websocketTraceRepository.add(trace);
		}
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		// Generate an error page if response getStatus code is not OK (200).
		if (res.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpHeaders.setContentLength(res, res.content().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Websocket error", cause);
		cause.printStackTrace();
		ctx.close();
	}

	private String getWebSocketLocation(FullHttpRequest req) {
		String location = req.headers().get(HOST) + properties.getWebsocketPath();
		if (properties.isSsl()) {
			return "wss://" + location;
		}
		else {
			return "ws://" + location;
		}
	}

}
