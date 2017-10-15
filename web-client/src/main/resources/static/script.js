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
			
			if ('pets' in data) {
				populatePetsList(data.pets);
			}
			

			
			//var ta = document.getElementById('responseText');
			//ta.value = ta.value + '\n' + event.data
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

function populatePetsList(pets) {
	var ul = document.getElementById("petsList");
	
	for (var i = 0; i < pets.length; i++) {
		
		var li = document.createElement("li");
		li.appendChild(document.createTextNode("Name: " + pets[i].name + ", description: " + pets[i].description + ", price: " + pets[i].value + " SEK"));
		li.className = "list-group-item";
		ul.appendChild(li);
		
	}
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
		
		var msg = JSON.stringify({
		  "type": "requestInventory"
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
