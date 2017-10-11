package se.lnu.services.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
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
	public String addToCart(@RequestBody AddToCart payload) {
		System.out.println(payload.toString());
		inventory.addToCartOutput().send(MessageBuilder.withPayload(payload).build());
		return "success";
	}
}
