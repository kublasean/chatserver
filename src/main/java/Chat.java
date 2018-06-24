package org.glassfish.samples.websocket.chat;

import org.glassfish.samples.websocket.chat.message.ChatMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;  

import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 

public class Chat {
	private Set<Session> members;
	private String name;
	private String userspan = "<span style=\"color: blue\">";
	public static final String SEPARATOR = ":";

	public Chat(String roomName) {
		this.members = Collections.newSetFromMap(new HashMap<Session, Boolean>());
		this.name = roomName;
	}
		
	public void notify(ChatMessage msg) throws IOException, EncodeException{
		for (Session client : this.members) {
			client.getBasicRemote().sendObject(msg);
		}
	}
	
	public void newMessage(String name, String contents) throws IOException, EncodeException {
		String msg = "<p>" + userspan + name + ":</span> " + contents + "</p>";
		notify(new ChatMessage(ChatMessage.CHAT_MESSAGE, " ", msg));
	}
	
	public void add(Session client) {
		this.members.add(client);
	}
	
	public void remove(Session client) {
		this.members.remove(client);
	}
	
	public String getName() { return this.name; }
}