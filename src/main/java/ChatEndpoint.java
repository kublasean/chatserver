/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
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
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@ServerEndpoint(value = "/chat", encoders = { ChatMessageEncoder.class }, decoders = { ChatMessageDecoder.class })
public class ChatEndpoint {
	
		private static Map<String, Chat> chatrooms = Collections.synchronizedMap(new HashMap<String, Chat>()); 
		
		@OnOpen
		public void onOpen(Session session) {
			
		}

		@OnClose
		public void onClose(Session session) {
			chatrooms.get(session.getUserProperties().get("roomId")).remove(session);
		}
		
		@OnMessage
		public void chat(ChatMessage message, Session client) throws IOException, EncodeException {
			switch (message.getType()) {
				case ChatMessage.CHAT_LIST_REQUEST:
					String msg = "";
					for (String name: chatrooms.keySet()) {
					msg += "<button onclick=\" joinChatRoom(\'" + name + "\')\">" + name + "</button>";
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
						room.newMessage(message.getData(), "has joined");
					}
					else {
						Chat newRoom = new Chat(roomId);
						newRoom.add(client);
						chatrooms.put(roomId, newRoom);
						newRoom.newMessage(message.getData(), "has joined");
					}
					break;
				case ChatMessage.CHAT_MESSAGE:
					chatrooms.get(message.getRoomId()).newMessage(client.getUserProperties().get("user").toString(), html2text(message.getData()));
					break;
			}
		}
		
		public static String html2text(String html) {
			return Jsoup.parse(html).text();
		}
		
}
