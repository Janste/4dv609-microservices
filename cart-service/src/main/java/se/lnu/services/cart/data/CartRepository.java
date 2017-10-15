package se.lnu.services.cart.data;

import java.util.List;

import se.lnu.service.common.message.AddToCart;
import se.lnu.service.common.message.RemoveFromCart;
import se.lnu.service.common.message.RequestCart;

public class CartRepository {

	public boolean addToCart(AddToCart request) {
		return CartAccessLayer.getInstance().addToCart(request);
	}
	
	public List<Integer> getCart(RequestCart request) {
		return CartAccessLayer.getInstance().getCartContents(request.getUserEmail());
	}
	
	public boolean removeFromCart(RemoveFromCart request) {
		return CartAccessLayer.getInstance().removePet(request);
	}
}
