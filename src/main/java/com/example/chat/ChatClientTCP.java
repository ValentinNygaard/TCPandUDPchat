package com.example.chat;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClientTCP {

    private final static int PORT = 55555;
    private final static String HOST = "127.0.0.1";

    private Socket clientSocket;
    private PrintWriter outStream;
    private BufferedReader inStream;
    private String clientName;

    private ChatClientTCP(String clientName) {
        this.clientName = clientName;
    }

    private void startConnection() {

        try {
            clientSocket = new Socket(HOST, PORT);
            outStream = new PrintWriter(clientSocket.getOutputStream(), true);
            inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startChat() {

        Thread senderThread = new Thread(new Sender());
        senderThread.start();

        while (true) {
            String msg = getMessage();
            String space = "                                        ";
            System.out.println(space + msg);
            if (msg.equals("Bye " + clientName + ", hope to see you again soon")) {
                break;
            }
        }
    }

    private class Sender implements Runnable {

        Sender() {}

        public void run() {

            sendMessage(clientName);

            Scanner input = new Scanner(System.in);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                sendMessage(line);
                if (line.equals("/bye")) {
                    break;
                }
            }
        }
    }

    private String getMessage() {

        try {
            return inStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMessage(String msg) {

        try {
            outStream.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopConnection() {

        try {
            inStream.close();
            outStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Required args: ClientName");
            System.exit(0);
        }

        ChatClientTCP client = new ChatClientTCP(args[0]);
        client.startConnection();
        client.startChat();
        client.stopConnection();
    }

}