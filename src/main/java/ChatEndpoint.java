package org.glassfish.samples.websocket.chat;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jsoup.Jsoup;

import org.glassfish.samples.websocket.chat.message.ChatMessage;
import org.glassfish.samples.websocket.chat.encoders.ChatMessageEncoder;
import org.glassfish.samples.websocket.chat.decoders.ChatMessageDecoder;



/**
 * Echo endpoint.
 * @author Pavel Bucek (pavel.bucek at oracle.com) mod. by Sean Whalen to chat endpoint
 */
@ServerEndpoint(value = "/chat", encoders = { ChatMessageEncoder.class }, decoders = { ChatMessageDecoder.class })
public class ChatEndpoint {
	
		private static Map<String, Chat> chatrooms = Collections.synchronizedMap(new HashMap<String, Chat>()); 
		
		@OnOpen
		public void onOpen(Session session) {
			
		}

		@OnClose
		public void onClose(Session session) throws IOException, EncodeException {
			Chat room = chatrooms.get(session.getUserProperties().get("roomId"));
			room.remove(session);
			room.newMessage(session.getUserProperties().get("user") + " has left.", "");
		}
		
		@OnMessage
		public void chat(ChatMessage message, Session client) throws IOException, EncodeException {
			switch (message.getType()) {
				case ChatMessage.CHAT_LIST_REQUEST:
					String msg = "";
					for (String name: chatrooms.keySet()) {
					msg += "<button onclick=\" joinChatRoom(\'" + name + "\')\">" + name + ": " + chatrooms.size() + "</button>";
					}
					client.getBasicRemote().sendObject(new ChatMessage(ChatMessage.CHAT_LIST_RESPONSE, " ", msg));
					break;
				case ChatMessage.JOIN_CHAT_REQUEST:
					String roomId = message.getRoomId();
					client.getUserProperties().put("roomId", roomId);
					client.getUserProperties().put("user", message.getData());
					
					if (chatrooms.containsKey(roomId)) {
						Chat room = chatrooms.get(roomId);
						room.add(client);
						room.newMessage(message.getData() + " 	has joined.", "");
					}
					else {
						Chat newRoom = new Chat(roomId);
						newRoom.add(client);
						chatrooms.put(roomId, newRoom);
						newRoom.newMessage(message.getData() + " has joined.", "");
					}
					break;
				case ChatMessage.CHAT_MESSAGE:
					chatrooms.get(message.getRoomId()).newMessage(client.getUserProperties().get("user").toString(), ": " + message.getData());
					break;
			}
		}
		
		public static String html2text(String html) {
			return Jsoup.parse(html).text();
		}
		
}
