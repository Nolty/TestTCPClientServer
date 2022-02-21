package ru.geekbrains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server online");
            Socket socket = serverSocket.accept();
            System.out.println("Client online");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                while (true) {
                    try {
                        String clientMessage = in.readUTF();
                        System.out.println("Client: " + clientMessage);

                        if (clientMessage.equalsIgnoreCase("quit")) {
                            out.writeUTF("quit");
                            System.out.println("Client said goodbye. Close connection");
                            in.close();
                            out.close();
                            socket.close();
                            System.exit(1);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Scanner sc = new Scanner(System.in);
            System.out.println("Type your messages here");

            while (true) {
                String text = sc.nextLine();
                out.writeUTF(text);

                if (text.equalsIgnoreCase("quit")) {
                    System.out.println("Server wants to quit. Close connection and shut down");
                    out.writeUTF("quit");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
