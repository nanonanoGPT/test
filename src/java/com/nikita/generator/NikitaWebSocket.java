/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
/*
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
*/
/**
 *
 * @author rkrzmail
 */
public class NikitaWebSocket extends HttpServlet {
    /*
    private static final long serialVersionUID = 1L;

    private static final String GUEST_PREFIX = "Guest";

    private final AtomicInteger connectionIds = new AtomicInteger(0);
    private final Set<ChatMessageInbound> connections =
            new CopyOnWriteArraySet<ChatMessageInbound>();

    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
        return new ChatMessageInbound(connectionIds.incrementAndGet());
    }

    private final class ChatMessageInbound extends MessageInbound {

        private final String nickname;

        private ChatMessageInbound(int id) {
            this.nickname = GUEST_PREFIX + id;
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
            connections.add(this);
            String message = String.format("* %s %s",
                    nickname, "has joined.");
            broadcast(message);
        }

        @Override
        protected void onClose(int status) {
            connections.remove(this);
            String message = String.format("* %s %s",
                    nickname, "has disconnected.");
            broadcast(message);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            // Never trust the client
            String filteredMessage = "ssss";
            broadcast(filteredMessage);
        }

        private void broadcast(String message) {
            for (ChatMessageInbound connection : connections) {
                try {
                    CharBuffer buffer = CharBuffer.wrap(message);
                    connection.getWsOutbound().writeTextMessage(buffer);
                } catch (IOException ignore) {
                    // Ignore
                }
            }
        }
    }
    
    */
}
