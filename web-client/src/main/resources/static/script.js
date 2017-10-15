window.onload = init;

var socket;
var serverUrl = "ws://localhost:9292/websocket";

var registerButtonPressed = false;
var loginButtonPressed = false;
var getInventoryButtonPressed = false;

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

function clearInventory() {
	document.getElementById("petsList").innerHTML = "";
}

function handleGetInventoryResponse(pets) {
	
	clearInventory();
	var ul = document.getElementById("petsList");
	
	for (var i = 0; i < pets.length; i++) {
		
		// New list item
		var li = document.createElement("li");
		li.appendChild(document.createTextNode("Name: " + pets[i].name + ", description: " + pets[i].description + ", price: " + pets[i].value + " SEK"));
		li.className = "list-group-item";
		li.style = "padding: 20px"
		
		// Add to cart button 
		if (getToken() != null) {
			var addToCartButton = document.createElement("button");
			addToCartButton.innerHTML = "Add to cart";
			addToCartButton.className = "btn btn-default pull-right";
			li.appendChild(addToCartButton);
		}
		
		// Append to list
		ul.appendChild(li);	
	}
	getInventoryButtonPressed = false;
}

function getToken() {
	var token = sessionStorage.getItem('token');
	return token;
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

		var msg = JSON.stringify({
			"type": "login",
			"user" : {
				"email" : "test@test.com",
				"password" : "password"
			}
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
