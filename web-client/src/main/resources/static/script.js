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
			else {
				console.log(data);
			}
			

			
			var ta = document.getElementById('responseText');
			ta.value = ta.value + '\n' + event.data
		};
		
		// After opening the websocket
		socket.onopen = function (event) {
			var ta = document.getElementById('responseText');
			ta.value = "Web Socket opened!";
			document.getElementById('message').removeAttribute("disabled");
			document.getElementById('sendButton').removeAttribute("disabled");
		};
		
		// When closing the websocket
		socket.onclose = function (event) {
			var ta = document.getElementById('responseText');
			ta.value = ta.value + "Web Socket closed";
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
	} else {
		document.getElementById('registerDiv').style.display = "inline";
		document.getElementById('loginDiv').style.display = "inline";
		document.getElementById('registerMenuItem').style.display = "inline";
		document.getElementById('loginMenuItem').style.display = "inline";
		document.getElementById('logoutMenuItem').style.display = "none";
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
	console.log(data);
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

function send(message) {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		/*var msg = JSON.stringify({
			"token": i++ + " ",
			"type": "addToCart",
			"pet" : 
			{
				"@type" : "Cat",
				"name" : "Mew",
				"value" : 14124,
				"type" : "Siberian",
				"id": i,
			}
		});
		*/
		
		/*var msg = JSON.stringify({
		  "type": "requestInventory"
		});*/

		/*var msg = JSON.stringify({
			"type": "login",
			"user" : {
				"email" : "test@test.com",
				"password" : "password"
			}
		});*/

		var msg = JSON.stringify({
			"type": "completeOrder",
			"token": getToken()
		});
		
		/*
			var msg = JSON.stringify({
                "token": "test",
                "type": "removeFromCart",
                "pet" : 
                {
                    "@type" : "Cat",
                    "name" : "Mew",
                    "value" : 14124,
                    "type" : "Siberian",
                    "id": i++,
                }
            });
		*/
		
		
		/*
		var msg = JSON.stringify({
		  "type": "requestCart",
		  "token": "test"
		});
		
		*/
		
		/*
		"user" : 
			{
				"firstName" : "Robert",
				"secondName" : "Nilsson",
				"streetAddress" : "Street 12",
				"city" : "Stockholm",
				"state" : "-",
				"zipCode" : "12345",
				"country" : "Sweden",
				"telephone" : "987654321",
				"email" : "robert.nilsson@gmail.com",
				"password" : "my_password"
			}
		*/
		
		socket.send(msg);
	} else {
		alert("The socket is not open.");
	}
}
