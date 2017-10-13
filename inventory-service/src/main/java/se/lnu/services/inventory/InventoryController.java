package se.lnu.services.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.lnu.service.common.channels.Inventory;
import se.lnu.service.common.message.AddToCart;

@EnableBinding(Inventory.class)
@RestController
public class InventoryController {

	@Autowired
	private Inventory inventory;
	
	@RequestMapping(path="/addToCart", method=RequestMethod.POST)
	public ResponseEntity<?> addToCart(@RequestHeader HttpHeaders headers, @RequestBody AddToCart payload) {
		System.out.println(headers.toString());
		List<String> tokens = headers.get("token");
		if (tokens == null || tokens.size() <= 0) {
			
			return new ResponseEntity<>("{ \"error\" : \"Invalid token.\"}", HttpStatus.FORBIDDEN);
		}
		System.out.println(payload.toString());
		inventory.addToCartOutput().send(MessageBuilder.withPayload(payload).build());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
