package org.glassfish.samples.websocket.chat.message;

public class ChatMessage {
	public static final String CHAT_LIST_REQUEST = "lreq";
	public static final String CHAT_LIST_RESPONSE = "lres";
	public static final String JOIN_CHAT_REQUEST = "jreq";
	public static final String JOIN_CHAT_RESPONSE = "jres";
	public static final String CHAT_MESSAGE = "cmsg";
	
	private final String type;
	private final String data;
	private final String roomId;
	
	public ChatMessage(String type, String roomId, String data) {
		this.type = type;
		this.roomId = roomId;
		this.data = data;
	}
	
	public String getType() { return this.type; }
	public String getData() { return this.data; }
	public String getRoomId() { return this.roomId; }
	
	@Override
	public String toString() {
		return this.type + ":" + this.roomId + ":" + this.data;
	}
	
	
}
