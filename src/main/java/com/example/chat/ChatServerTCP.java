package com.example.chat;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ChatServerTCP {

    private final static int PORT = 55555;

    private ServerSocket serverSocket;

    private static Set<PrintWriter> currentClientStreams = new HashSet<>();

    private void start() {

        try {
            serverSocket = new ServerSocket(PORT);
            while (true)
                new ChatClientHandler(serverSocket.accept()).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            stop();
        }
    }

    private void stop() {

        try {
            serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ChatClientHandler extends Thread {

        private Socket clientSocket;
        private PrintWriter outStream;
        private BufferedReader inStream;

        ChatClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                outStream = new PrintWriter(clientSocket.getOutputStream(), true);
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String clientName = inStream.readLine();
                for (PrintWriter writer : currentClientStreams) {
                    writer.println("--- " + clientName + " has joined the chat");
                }
                currentClientStreams.add(outStream);
                System.out.println("--- " + clientName + " has joined the chat");
                outStream.println("Hi " + clientName + ", welcome to the chat");

                String inputLine;
                while ((inputLine = inStream.readLine()) != null) {
                    if ("/bye".equals(inputLine)) {
                        System.out.println("--- " + clientName + " has left the chat");
                        outStream.println("Bye " + clientName + ", hope to see you again soon");
                        currentClientStreams.remove(outStream);
                        for (PrintWriter writer : currentClientStreams) {
                            writer.println("--- " + clientName + " has left the chat");
                        }
                        break;
                    }
                    System.out.println(clientName + ": " + inputLine);
                    for (PrintWriter writer : currentClientStreams) {
                        writer.println(clientName + ": " + inputLine);
                    }
                }
                inStream.close();
                outStream.close();
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServerTCP server = new ChatServerTCP();
        server.start();
    }

}