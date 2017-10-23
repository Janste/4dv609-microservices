window.onload = init;

var socket;
var serverUrl = "ws://localhost:9292/websocket";

function init() {
	connectoToWebsocket();
}

function connectoToWebsocket() {
	if (!window.WebSocket) {
		window.WebSocket = window.MozWebSocket;
	}
	if (window.WebSocket) {
		socket = new WebSocket(serverUrl);
		
		// When message arrives
		socket.onmessage = function (event) {
			
			var data = JSON.parse(event.data);
			
			var type = data["type"];

			switch (type) {
				case "requestInventory":
					handleGetInventoryResponse(data.pets);
					break;
				case "register":
				case "login":
					handleRegisterOrLoginResponse(data);
					break;
				case "requestCart":
					handleGetShoppingCartResponse(data.pets);
					break;
				case "addToCart":
					handleAddToCart(data);
					break;
				case "removeFromCart":
					handleRemoveFromCart(data);
					break;
				case "completeOrder":
					handleOrderResponse(data);
					break;
				case "updateUser":
					handleChangeUserDataResponse(data);
					break;
				case "getUser":
					handleGetUserDataResponse(data);
					break;
				default:
					console.log("Unknown response");
					console.log(data);
			}			
		};
		
		// After opening the websocket
		socket.onopen = function (event) {
			console.log("Web Socket opened!");
			getInventory();
			changeViewBetweenLoggedInAndLoggedOut();
		};
		
		// When closing the websocket
		socket.onclose = function (event) {
			console.log("Web Socket closed");
		};
	} else {
		alert("Your browser does not support Web Socket.");
	}
}

function hideRegister() {
	document.getElementById('registerDiv').style.display = "none";
}

function hideLogin() {
	document.getElementById('loginDiv').style.display = "none";
}

function hideCart() {
	document.getElementById('shoppingCartDiv').style.display = "none";
}

function hidePets() {
	document.getElementById('inventoryDiv').style.display = "none";
}

function hideSettings() {
	document.getElementById('changeUserDataDiv').style.display = "none";
}

function showInventory() {
	hideRegister();
	hideLogin();
	hideCart();
	hideSettings();
	document.getElementById('inventoryDiv').style.display = "inline";
	getInventory();
}

function showLogin() {
	hideRegister();
	hideCart();
	hideSettings();
	hidePets();
	document.getElementById('loginDiv').style.display = "inline";
}

function showRegister() {
	hideLogin();
	hideCart();
	hideSettings();
	hidePets();
	document.getElementById('registerDiv').style.display = "inline";
}

function showCart() {
	hideLogin();
	hideRegister();
	hideSettings();
	hidePets();
	document.getElementById('shoppingCartDiv').style.display = "inline";
	getShoppingCart();
}

function showEdit() {
	hideLogin();
	hideRegister();
	hideCart();
	hidePets();
	document.getElementById('changeUserDataDiv').style.display = "inline";
	openChangeUserDataForm();
}

function changeViewBetweenLoggedInAndLoggedOut() {
	
	var token = getToken();
	
	if (token != null) {
		hideRegister();
		hideLogin();
		showInventory();
		document.getElementById('registerMenuItem').style.display = "none";
		document.getElementById('loginMenuItem').style.display = "none";
		document.getElementById('logoutMenuItem').style.display = "inline";
		document.getElementById('editUser').style.display = "inline";
	} else {
		showLogin();
		document.getElementById('registerMenuItem').style.display = "inline";
		document.getElementById('loginMenuItem').style.display = "inline";
		document.getElementById('logoutMenuItem').style.display = "none";
		document.getElementById('editUser').style.display = "none";
	}
}

function register() {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
			"type": "register",
			"user" : 
			{
				"firstName" : document.getElementById('registerFirstName').value,
				"secondName" : document.getElementById('registerSecondName').value,
				"streetAddress" : document.getElementById('registerStreetAddress').value,
				"city" : document.getElementById('registerCity').value,
				"state" : document.getElementById('registerState').value,
				"zipCode" : document.getElementById('registerZipCode').value,
				"country" : document.getElementById('registerCountry').value,
				"telephone" : document.getElementById('registerTelephone').value,
				"email" : document.getElementById('registerEmail').value,
				"password" : document.getElementById('registerPassword').value
			}
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function login() {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
			"type": "login",
			"user" : 
			{
				"email" : document.getElementById('loginEmail').value,
				"password" : document.getElementById('loginPassword').value
			}
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function logout() {
	sessionStorage.clear();
	location.reload();
}

function handleRegisterOrLoginResponse(data) {

	if (data.success == true) {
		
		sessionStorage.setItem('token', data.user.token);
		sessionStorage.setItem('email', data.user.email);
		changeViewBetweenLoggedInAndLoggedOut();
	} 
	else {
		if (data.type == "register") {
			document.getElementById('registerMessageToUser').style.color = "red";
			document.getElementById('registerMessageToUser').textContent = data.error;
		}
		else if (data.type == "login") {
			document.getElementById('loginMessageToUser').style.color = "red";
			document.getElementById('loginMessageToUser').textContent = data.error;
		}
	}
}

function getInventory() {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "requestInventory"
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function handleGetInventoryResponse(pets) {
	
	clearInventory();
	var ul = document.getElementById("petsList");
	for (var i = 0; i < pets.length; i++) {
		
		// New list item
		var li = document.createElement("li");
		
		var pet = pets[i];
		
		li.appendChild(document.createTextNode("Name: " + pet.name + ", type: " + pet.type + ", description: " + pet.description + ", price: " + pet.value + " SEK"));
		li.className = "list-group-item";
		li.style = "padding: 20px"
		
		// Add to cart button 
		if (getToken() != null) {
			var addToCartButton = document.createElement("button");
			addToCartButton.innerHTML = "Add to cart";
			addToCartButton.className = "btn btn-default pull-right";
			
			addToCartButton.setAttribute("pettype", pet.type);
			addToCartButton.setAttribute("atpettype", pet["@type"]);
			addToCartButton.setAttribute("petid", pet.id);
			addToCartButton.setAttribute("petname", pet.name);
			addToCartButton.setAttribute("petvalue", pet.value);
			
			addToCartButton.setAttribute("onclick", "addToCart(this);");
			li.appendChild(addToCartButton);
		}
		
		// Append to list
		ul.appendChild(li);	
	}
}

function getShoppingCart() {
	if (getToken() == null) {
		setShoppingCartMessage("You need to log in first before you can add items to your shopping cart.");
		return;
	}
	
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "requestCart",
		  "token": getToken()
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function handleGetShoppingCartResponse(pets) {
	
	clearShoppingCart();
	var ul = document.getElementById("shoppingCartList");
	
	for (var i = 0; i < pets.length; i++) {
		
		var pet = pets[i];
		
		// New list item
		var li = document.createElement("li");
		li.appendChild(document.createTextNode("Name: " + pet.name + ", type: " + pet.type + ", description: " + pet.description + ", price: " + pet.value + " SEK"));
		li.className = "list-group-item";
		li.style = "padding: 20px"
		
		// Add to cart button 
		var removeFromCartButton = document.createElement("button");
		removeFromCartButton.innerHTML = "Remove";
		removeFromCartButton.className = "btn btn-default pull-right";
		
		removeFromCartButton.setAttribute("pettype", pet.type);
		removeFromCartButton.setAttribute("atpettype", pet["@type"]);
		removeFromCartButton.setAttribute("petid", pet.id);
		removeFromCartButton.setAttribute("petname", pet.name);
		removeFromCartButton.setAttribute("petvalue", pet.value);
		
		removeFromCartButton.setAttribute("onclick", "removeFromCart(this);");
		li.appendChild(removeFromCartButton);
		
		
		// Append to list
		ul.appendChild(li);	
		
		
	}
	
	if (pets.length === 0) {
		setShoppingCartMessage("There are no pets in your shopping cart");
	}
}

function addToCart(data) {

	var id = data.getAttribute("petid");
	var type = data.getAttribute("pettype");
	var attype = data.getAttribute("atpettype");
	var name = data.getAttribute("petname");
	var value = data.getAttribute("petvalue");

	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "addToCart",
		  "token": getToken(),
		  "pet": {
			  "@type": attype,
			  "name": name,
			  "value": value,
			  "type": type,
			  "id": id
		  }
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function handleAddToCart(data) {
	
	if (data.success == true) {
		document.getElementById('inventoryMessageToUser').textContent = "Successfully added to cart.";
	} 
	else {
		document.getElementById('inventoryMessageToUser').textContent = data.error;
	}
}

function removeFromCart(data) {
	var id = data.getAttribute("petid");
	var type = data.getAttribute("pettype");
	var attype = data.getAttribute("atpettype");
	var name = data.getAttribute("petname");
	var value = data.getAttribute("petvalue");

	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "removeFromCart",
		  "token": getToken(),
		  "pet": {
			  "@type": attype,
			  "name": name,
			  "value": value,
			  "type": type,
			  "id": id
		  }
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function handleRemoveFromCart(data) {
	
	if (data.success == true) {
		getShoppingCart();
	}
}

function order() {
	
	var amount = document.getElementById("shoppingCartList").getElementsByTagName("li").length;
	
	if (amount == 0) {
		setShoppingCartMessage("You need to add pets to the cart before you can buy.");
	} 
	else {
		if (!window.WebSocket) {
		return;
		}
		if (socket.readyState == WebSocket.OPEN) {

			var msg = JSON.stringify({
			  "type": "completeOrder",
			  "token": getToken()
			});
			socket.send(msg);
		} else {
			console.log("The socket is not open.");
		}
	}
}

function handleOrderResponse(data) {
	
	if (data.success == true) {
		clearShoppingCart();
		getInventory();
		setShoppingCartMessage("You have successfully placed your order.");
	} 
	else {
		setShoppingCartMessage("Something went wrong. Operation could not be completed.");
	} 
}

function openChangeUserDataForm() {
	
	document.getElementById('changeUserDataForm').style.display = "inline";
	
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "getUser",
		  "token": getToken()
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}	
}

function handleGetUserDataResponse(data) {
	if (data.success == true) {
		document.getElementById('changeFirstName').value = data.user.firstName;
		document.getElementById('changeSecondName').value = data.user.secondName;
		document.getElementById('changeStreetAddress').value = data.user.streetAddress;
		document.getElementById('changeCity').value = data.user.city;
		document.getElementById('changeState').value = data.user.state;
		document.getElementById('changeZipCode').value = data.user.zipCode;
		document.getElementById('changeCountry').value = data.user.country;
		document.getElementById('changeTelephone').value = data.user.telephone;
		document.getElementById('changeEmail').value = data.user.email;
	} 
	else {
		document.getElementById('changeUserMessageToUser').textContent = "An error occured."; 
	} 
}

function changeUserData() {
	
	if (areFieldsEmptyInChangeUserDataForm()) {
		document.getElementById('changeUserMessageToUser').textContent = "All fields must be filled in";
		return;
	}
	
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {

		var msg = JSON.stringify({
		  "type": "updateUser",
		  "token": getToken(),
		  "user": {
			"firstName" : document.getElementById('changeFirstName').value,
			"secondName" : document.getElementById('changeSecondName').value,
			"streetAddress" : document.getElementById('changeStreetAddress').value,
			"city" : document.getElementById('changeCity').value,
			"state" : document.getElementById('changeState').value,
			"zipCode" : document.getElementById('changeZipCode').value,
			"country" : document.getElementById('changeCountry').value,
			"telephone" : document.getElementById('changeTelephone').value,
			"email" : document.getElementById('changeEmail').value,
			"password" : document.getElementById('changePassword').value
		}
		});
		socket.send(msg);
	} else {
		console.log("The socket is not open.");
	}
}

function areFieldsEmptyInChangeUserDataForm() {
	var fn = document.getElementById('changeFirstName').value;
	var sn = document.getElementById('changeSecondName').value;
	var sr = document.getElementById('changeStreetAddress').value;
	var ci = document.getElementById('changeCity').value;
	var st = document.getElementById('changeState').value;
	var zc = document.getElementById('changeZipCode').value;
	var co = document.getElementById('changeCountry').value;
	var te = document.getElementById('changeTelephone').value;
	var em = document.getElementById('changeEmail').value;
	var pa = document.getElementById('changePassword').value;
	
	return (fn == "" || sn == "" || sr == "" || ci == "" || st == "" || zc == "" || co == "" || te == "" || em == "" || pa == "");
}

function handleChangeUserDataResponse(data) {

	if (data.success == true) {
		
		document.getElementById('changeUserMessageToUser').textContent = "Your data has been successfully changed"
		
		document.getElementById('changeFirstName').value = data.user.firstName;
		document.getElementById('changeSecondName').value = data.user.secondName;
		document.getElementById('changeStreetAddress').value = data.user.streetAddress;
		document.getElementById('changeCity').value = data.user.city;
		document.getElementById('changeState').value = data.user.state;
		document.getElementById('changeZipCode').value = data.user.zipCode;
		document.getElementById('changeCountry').value = data.user.country;
		document.getElementById('changeTelephone').value = data.user.telephone;
		document.getElementById('changeEmail').value = data.user.email;
	} 
	else {
		document.getElementById('changeUserMessageToUser').textContent = data.error; 
	}
}

function getToken() {
	var token = sessionStorage.getItem('token');
	return token;
}

function clearInventory() {
	document.getElementById("petsList").innerHTML = "";
}

function clearShoppingCart() {
	document.getElementById("shoppingCartList").innerHTML = "";
	document.getElementById('inventoryMessageToUser').textContent = "";
	setShoppingCartMessage("");
}

function setShoppingCartMessage(msg) {
	document.getElementById('shoppingCartMessageToUser').textContent = msg;
}

