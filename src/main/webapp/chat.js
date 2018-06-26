function chat(uri, username) {
	var websocket = new WebSocket(uri);
	var output = null;
	
	this.username = username;
	this.roomId = null;
	
	const CHAT_LIST_REQUEST = "lreq";
	const CHAT_LIST_RESPONSE = "lres";
	const JOIN_CHAT_REQUEST = "jreq";
	const JOIN_CHAT_RESPONSE = "jres";
	const CHAT_MESSAGE = "cmsg";
	const SEPARATOR = ":";
	
	
	function message(type, roomId, msg) {
		return type + SEPARATOR + roomId + SEPARATOR + msg;
	}
	function parse(msg) {
		console.log(msg);
		let fields = splitKeep(msg, SEPARATOR, 3);
		return { type: fields[0], roomId: fields[1], data: fields[2] }
	}
	function appendOutput(data) {
		output.innerHTML += data;
	}
	function clearOutput() {
		output.innerHTML = "";
	}
	
	this.setOutput = function(out) {
		output = out;
		console.log(output);
	}
	
	function chatListRequest() {
		websocket.send(message(CHAT_LIST_REQUEST, " ", " "));
	}
	
	this.joinChatRoom = function(roomId) {
		this.roomId = roomId;
		websocket.send(message(JOIN_CHAT_REQUEST, roomId, this.username));
	}
	
	this.sendMessage = function(msg) {
		console.log(msg);
		websocket.send(message(CHAT_MESSAGE, this.roomId, msg));
	}
	
	websocket.onopen = function (evt) {
		console.log("connection established");
		chatListRequest();
	};
	
	websocket.onmessage = function (evt) {
		let msg = parse(evt.data);
		console.log(msg);
		appendOutput(msg.data);
		output.scrollTop += 100;
	};
	
	websocket.onerror = function (evt) {
			console.log(evt.data);
	};
	
}

function splitKeep(m, sep, limit) {
	let ar = [];
	let start = 0;
	let end = 0;
	
	for (let i=0; i<limit-1; i++) {
		end = m.indexOf(sep, start);
		ar.push(m.substring(start, end));
		start = end+1;
	}
	ar.push(m.substring(start, m.length));
	return ar;
}


