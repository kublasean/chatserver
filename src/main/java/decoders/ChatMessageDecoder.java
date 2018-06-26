package org.glassfish.samples.websocket.chat.decoders;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.glassfish.samples.websocket.chat.message.ChatMessage;

/**
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 */
public class ChatMessageDecoder implements Decoder.Text<ChatMessage> {

    @Override
    public ChatMessage decode(String s) {
        String[] tokens = s.split(":");
				String contents = tokens[2];
				for (int i=3; i<tokens.length; i++) {
					contents += ":" + tokens[i];
				}

        return new ChatMessage(tokens[0], tokens[1], contents);
    }

    @Override
    public boolean willDecode(String s) {
        return
                s.startsWith(ChatMessage.CHAT_LIST_REQUEST) ||
                s.startsWith(ChatMessage.JOIN_CHAT_REQUEST) ||
                s.startsWith(ChatMessage.CHAT_MESSAGE);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // do nothing.
    }

    @Override
    public void destroy() {
        // do nothing.
    }
}
