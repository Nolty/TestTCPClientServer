package ru.geekbrains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 8080;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }

    public void connect() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread reader = readMessageThread();
            reader.start();

            Scanner sc = new Scanner(System.in);

            System.out.println("Type your messages here");

            while (true) {
                String text = sc.nextLine();
                sendMessage(text);

                if (text.equalsIgnoreCase("quit")) {
                    System.out.println("Client wants to quit");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public Thread readMessageThread () {
            return new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = in.readUTF();
                        if (serverMessage.equalsIgnoreCase("quit")) {
                            System.out.println("Server said goodbye. Close connection and shut down");
                            closeConnection();
                            break;
                        }

                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public synchronized void closeConnection () {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void sendMessage (String text) throws IOException {
            out.writeUTF(text);
        }
    }
