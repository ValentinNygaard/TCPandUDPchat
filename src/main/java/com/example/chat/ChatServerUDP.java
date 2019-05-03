package com.example.chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerUDP {

    private final static int PORT = 4444;
    private final static int BUFFER = 1024;

    private DatagramSocket socket;
    private ArrayList<InetAddress> clientAddresses;
    private ArrayList<Integer> clientPorts;
    private HashSet<String> existingClients;

    private ChatServerUDP() {

        try {
            socket = new DatagramSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientAddresses = new ArrayList();
        clientPorts = new ArrayList();
        existingClients = new HashSet();
    }

    private void start() {

        while (true) {
            try {
                byte[] buffer = new byte[BUFFER];
                Arrays.fill(buffer, (byte)0);
                // Receive datagram
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                // Get data from datagram
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String message = new String(buffer, 0, buffer.length);
                // Add any new client to existing clients
                String id = clientAddress.toString() + ":" + clientPort;
                if (!existingClients.contains(id)) {
                    existingClients.add(id);
                    clientPorts.add(clientPort);
                    clientAddresses.add(clientAddress);
                }
                // Show client message in server console
                System.out.println(id + " : " + message);
                // Preparing client message for datagram
                byte[] data = (id + " : " +  message).getBytes();
                // Sending client message to all clients
                for (int i=0; i < clientAddresses.size(); i++) {
                    InetAddress address = clientAddresses.get(i);
                    int port = clientPorts.get(i);
                    packet = new DatagramPacket(data, data.length, address, port);
                    socket.send(packet);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        ChatServerUDP server = new ChatServerUDP();
        server.start();
    }
}