package gui;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ServerSocket serverSocket;
        Socket socket;
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        int enemyX = 50, enemyY = 50;
        int playerX, playerY;
        Random random = new Random(System.currentTimeMillis());
        playerX = Math.abs(random.nextInt()) % 350 + 50;
        playerY = Math.abs(random.nextInt()) % 350 + 50;

        try {
            serverSocket = new ServerSocket(1234);
            socket = serverSocket.accept();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeInt(playerX);
            objectOutputStream.flush();
            objectOutputStream.writeInt(playerY);
            objectOutputStream.flush();

            enemyX = objectInputStream.readInt();
            enemyY = objectInputStream.readInt();

        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Server");
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        Group group = new Group();
        Scene scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.setResizable(false);
        scene.setCursor(Cursor.NONE);
        primaryStage.show();

        new MainBoard(group, scene, objectOutputStream, objectInputStream ,
                playerX, playerY, enemyX, enemyY, random);

    }
}
