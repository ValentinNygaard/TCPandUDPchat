package com.example.chat;

import java.io.*;
import java.net.*;

public class ChatClientUDP {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 4444;
    private final static int BUFFER = 1024;
    private DatagramSocket socket;

    private void start() {

        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread receiverThread = new Thread(new MessageReceiver(socket));
        Thread senderThread = new Thread(new MessageSender(socket));
        receiverThread.start();
        senderThread.start();
    }

    private class MessageSender implements Runnable {

        private DatagramSocket socket;

        MessageSender(DatagramSocket socket) {
            this.socket = socket;
        }

        private void sendMessage(String message) {

            try {
                byte buf[] = message.getBytes();
                InetAddress address = InetAddress.getByName(HOST);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {

            boolean connected = false;
            do {
                try {
                    sendMessage("HELLO");
                    connected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (!connected);

            BufferedReader inStream = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    while (!inStream.ready()) {
                        Thread.sleep(100);
                    }
                    sendMessage(inStream.readLine());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MessageReceiver implements Runnable {

        DatagramSocket socket;
        byte buffer[];

        MessageReceiver(DatagramSocket socket) {
            this.socket = socket;
            buffer = new byte[BUFFER];
        }

        public void run() {

            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    String space = "                                        ";
                    System.out.println(space + received);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        ChatClientUDP client = new ChatClientUDP();
        client.start();
    }

}



