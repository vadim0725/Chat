package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
            try {
                pair.getValue().send(message);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    pair.getValue().send(new Message(MessageType.TEXT, "Не удалось отправить сообщение!"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        int port = ConsoleHelper.readInt();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен!");
            while (true) {
                socket = serverSocket.accept();
                new Handler(socket).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка!");
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            System.out.println(socket.getRemoteSocketAddress());
            try {
                Connection connection = new Connection(socket);
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом.");
            }

        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message messageIn;
            do {
                connection.send(new Message(MessageType.NAME_REQUEST));
                messageIn = connection.receive();
            } while (messageIn.getType() != MessageType.USER_NAME || messageIn.getData().isEmpty() || connectionMap.containsKey(messageIn.getData()));
            connectionMap.put(messageIn.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return messageIn.getData();
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (!name.equals(userName)){
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String messageText = userName + ": " + message.getData();
                    Message messageNew = new Message(MessageType.TEXT, messageText);
                    sendBroadcastMessage(messageNew);
                } else {
                    ConsoleHelper.writeMessage("Нe правильный формат сообщения!");
                }
            }
        }
    }
}
