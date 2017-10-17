window.onload = init;

var socket;
var serverUrl = "ws://localhost:9292/websocket";

var registerButtonPressed = false;
var loginButtonPressed = false;
var getInventoryButtonPressed = false;
var getShoppingCartButtonPressed = false;
var addToShoppingCartButtonPressed = false;
var removeFromShoppingCartButtonPressed = false;
var orderButtonPressed = false;
var openChangeUserDataFormButtonPressed = false;
var changeUserDataButtonPressed = false;

function init() {
	connectoToWebsocket();
	changeViewBetweenLoggedInAndLoggedOut();
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
			
			if (getInventoryButtonPressed) {
				handleGetInventoryResponse(data.pets);
			}
			else if (registerButtonPressed) {
				handleRegisterOrLoginResponse(data);
			}
			else if (loginButtonPressed) {
				handleRegisterOrLoginResponse(data);
			}
			else if (getShoppingCartButtonPressed) {
				handleGetShoppingCartResponse(data.pets);
			}
			else if (addToShoppingCartButtonPressed) {
				handleAddToCart(data);
			}
			else if (removeFromShoppingCartButtonPressed) {
				handleRemoveFromCart(data);
			}
			else if (orderButtonPressed) {
				handleOrderResponse(data);
			}
			else if (changeUserDataButtonPressed) {
				handleChangeUserDataResponse(data);
			}
			else if (openChangeUserDataFormButtonPressed) {
				handleGetUserDataResponse(data);
			}
			else {
				console.log("Unknown response from server: ");
				console.log(data);
			}
			
		};
		
		// After opening the websocket
		socket.onopen = function (event) {
			console.log("Web Socket opened!");
		};
		
		// When closing the websocket
		socket.onclose = function (event) {
			console.log("Web Socket closed");
		};
	} else {
		alert("Your browser does not support Web Socket.");
	}
}

function changeViewBetweenLoggedInAndLoggedOut() {
	
	var token = getToken();
	
	if (token != null) {
		document.getElementById('registerDiv').style.display = "none";
		document.getElementById('loginDiv').style.display = "none";
		document.getElementById('registerMenuItem').style.display = "none";
		document.getElementById('loginMenuItem').style.display = "none";
		document.getElementById('logoutMenuItem').style.display = "inline";
		document.getElementById('changeUserDataDiv').style.display = "inline";
	} else {
		document.getElementById('registerDiv').style.display = "inline";
		document.getElementById('loginDiv').style.display = "inline";
		document.getElementById('registerMenuItem').style.display = "inline";
		document.getElementById('loginMenuItem').style.display = "inline";
		document.getElementById('logoutMenuItem').style.display = "none";
		document.getElementById('changeUserDataDiv').style.display = "none";
	}
	clearInventory();
	clearShoppingCart();
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
		registerButtonPressed = true;
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
		loginButtonPressed = true;
	} else {
		console.log("The socket is not open.");
	}
}

function logout() {
	sessionStorage.clear();
}

function handleRegisterOrLoginResponse(data) {

	if (data.success == true) {
		
		sessionStorage.setItem('token', data.user.token);
		sessionStorage.setItem('email', data.user.email);
		
	} 
	else {
		if (registerButtonPressed) {
			document.getElementById('registerMessageToUser').style.color = "red";
			document.getElementById('registerMessageToUser').textContent = data.error;
		}
		else if (loginButtonPressed) {
			document.getElementById('loginMessageToUser').style.color = "red";
			document.getElementById('loginMessageToUser').textContent = data.error;
		}
	}
	registerButtonPressed = false;
	loginButtonPressed = false;
	changeViewBetweenLoggedInAndLoggedOut();
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
		getInventoryButtonPressed = true;
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
	getInventoryButtonPressed = false;
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
		getShoppingCartButtonPressed = true;
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
	
	getShoppingCartButtonPressed = false;
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
		addToShoppingCartButtonPressed = true;
	} else {
		console.log("The socket is not open.");
	}
}

function handleAddToCart(data) {
	
	if (data.success == true) {
		getShoppingCart();
		document.getElementById('inventoryMessageToUser').textContent = "";
	} 
	else {
		document.getElementById('inventoryMessageToUser').textContent = data.error;
	}
		
	addToShoppingCartButtonPressed = false;
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
		removeFromShoppingCartButtonPressed = true;
	} else {
		console.log("The socket is not open.");
	}
}

function handleRemoveFromCart(data) {
	
	if (data.success == true) {
		getShoppingCart();
	}
	
	removeFromShoppingCartButtonPressed = false;
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
			orderButtonPressed = true;
		} else {
			console.log("The socket is not open.");
		}
	}
}

function handleOrderResponse(data) {
	
	if (data.success == true) {
		getShoppingCart();
		setShoppingCartMessage("You can successfully placed your order.");
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
		openChangeUserDataFormButtonPressed = true;
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
	openChangeUserDataFormButtonPressed = false;
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
		changeUserDataButtonPressed = true;
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
	changeUserDataButtonPressed = false;
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
	setShoppingCartMessage("");
}

function setShoppingCartMessage(msg) {
	document.getElementById('shoppingCartMessageToUser').textContent = msg;
}

