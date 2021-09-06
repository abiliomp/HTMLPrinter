/*
 * The MIT License
 *
 * Copyright 2021 Networkers SRL.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package py.com.nw.htmlprinter;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author abiliomp
 */
public class PrintServer extends WebSocketServer {
    public PrintServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        System.out.println("[PrintServer] Started websocket server on port: " + port);
    }

    public PrintServer(InetSocketAddress address) {
        super(address);
        System.out.println("[PrintServer] Started websocket server on address: " + address.toString());
    }

    public PrintServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
        System.out.println("[PrintServer] Started websocket server with specific draft on port: " + port);
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[PrintServer] Connection received from: " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("[PrintServer] Connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has been closed.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        PrintRequestHandler prh = new PrintRequestHandler(conn, message);
        Thread t = new Thread(prh);
        t.setName("PrintService-" + conn.getResourceDescriptor());
        t.start();
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        PrintRequestHandler prh = new PrintRequestHandler(conn, StandardCharsets.UTF_8.decode(message).toString());
        Thread t = new Thread(prh);
        t.setName("PrintService-" + conn.getResourceDescriptor());
        t.start();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[PrinterServer ERR] Error ocurred: " + ex.getLocalizedMessage());
        System.err.println("[PrinterServer ERR] Stack trace: ");
        ex.printStackTrace();
        if (conn != null) {
            PrintResponseMessage prm = new PrintResponseMessage(PrintResponseMessage.PRINTER_STATUS_ERROR, PrintResponseMessage.REQUEST_STATUS_REJECTED, "Error in the conection: " + ex.getLocalizedMessage());
            conn.send(prm.toJson()); 
        }
    }

    @Override
    public void onStart() {
        System.out.println("[PrinterServer] Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}