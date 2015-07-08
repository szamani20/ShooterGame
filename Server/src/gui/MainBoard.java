package gui;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Szamani on 7/7/2015.
 */
public class MainBoard {
    private Group group;
    private Scene scene;
    private final int width = 450;
    private final int height = 450;
    private int playerX;
    private int playerY;
    private int enemyX;
    private int enemyY;
    private int pointerX;
    private int pointerY;
    private boolean isRpg;

    private Random random;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private int playerHp;
    private int playerMana;
    private int enemyHp;
    private int enemyMana;
    private final int maxHp = 2500;
    private final int maxMana = 1000;
    private final int bulletHpReduction = 200;
    private final int rpgHpReduction = 500;
    private final int rpgManaCost = 250;
    private final int gameDifficulty = 200;

    private Label playerHpLabel;
    private Label playerManaLabel;
    private Label enemyHpLabel;
    private Label enemyManaLabel;
    private Canvas canvas;
    private GraphicsContext gc;
    private Rectangle playerHpBar;
    private Rectangle playerManaBar;
    private Rectangle enemyHpBar;
    private Rectangle enemyManaBar;

    private Circle rpgPermissionSign;
    private boolean rpgPermission;


    private Image enemyImage;
    private ImageView enemyImageView;
    private Image pointerImage;
    private ImageView pointerImageView;

    private PlayerInformation playerInformation;

    public MainBoard(Group group, Scene scene, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream,
                     int playerX, int playerY,  int enemyX, int enemyY, Random random) {
        this.group = group;
        this.scene = scene;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
        this.random = random;
        this.playerX = playerX;
        this.playerY = playerY;
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.random = random;

        playerHp = maxHp;
        playerMana = maxMana;
        enemyHp = maxHp;
        enemyMana = maxMana;
        playerHpLabel = new Label();
        playerHpLabel.setLayoutX(width / 2 - 45);
        playerHpLabel.setLayoutY(0);
        playerManaLabel = new Label();
        playerManaLabel.setLayoutX(width / 2 - 45);
        playerManaLabel.setLayoutY(25);
        enemyHpLabel = new Label();
        enemyHpLabel.setLayoutX(width / 2 + 155);
        enemyHpLabel.setLayoutY(0);
        enemyManaLabel = new Label();
        enemyManaLabel.setLayoutX(width / 2 + 155);
        enemyManaLabel.setLayoutY(25);
        rpgPermissionSign = new Circle(width / 2 + 20, 7, 5, Color.GREEN);
        rpgPermission = true;
        enemyImage = new Image(this.getClass().getResourceAsStream("/enemy.png"));
        enemyImageView = new ImageView(enemyImage);
        pointerImage = new Image(this.getClass().getResourceAsStream("/pointer.png"));
        pointerImageView = new ImageView(pointerImage);
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        gc.strokeRect(width / 2 - 150, 5, 100, 15);
        gc.strokeRect(width / 2 - 150, 25, 100, 15);
        gc.strokeRect(width / 2 + 50, 5, 100, 15);
        gc.strokeRect(width / 2 + 50, 25, 100, 15);

        playerHpBar = new Rectangle(width / 2 - 150, 5, 100, 15);
        playerManaBar = new Rectangle(width / 2 - 150, 25, 100, 15);
        enemyHpBar = new Rectangle(width / 2 + 50, 5, 100, 15);
        enemyManaBar = new Rectangle(width / 2 + 50, 25, 100, 15);

        playerHpBar.setFill(Color.RED);
        playerManaBar.setFill(Color.LIGHTBLUE);
        enemyHpBar.setFill(Color.RED);
        enemyManaBar.setFill(Color.LIGHTBLUE);

        playerInformation = new PlayerInformation(playerHp, playerMana, playerX, playerY);

        group.getChildren().addAll(playerHpLabel, playerManaLabel, enemyHpLabel, enemyManaLabel, rpgPermissionSign,
                enemyImageView, pointerImageView, canvas, playerHpBar, playerManaBar, enemyHpBar, enemyManaBar);

        playerHpLabel.setText(String.valueOf(playerHp));
        playerManaLabel.setText(String.valueOf(playerMana));
        enemyHpLabel.setText(String.valueOf(enemyHp));
        enemyManaLabel.setText(String.valueOf(enemyMana));

        catchSignal();
        setBoardListener();
        setTimer();
    }

    private void setTimer() {
        // too delay to manage

//        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1500), event -> {
//            sendPlayerInformation();
//        }));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendPlayerInformation();
            }
        }, 20, gameDifficulty);
    }

    private void sendPlayerInformation() {
        playerInformation = null;
        playerMana += 20;
        if (playerMana >= 1000)
            playerMana = 1000;
        playerX = Math.abs(random.nextInt()) % (width - 100) + 50;
        playerY = Math.abs(random.nextInt()) % (height - 100) + 50;
        playerInformation = new PlayerInformation(playerHp, playerMana, playerX, playerY);

        Platform.runLater(() -> {
            playerManaLabel.setText(String.valueOf(playerMana));
            playerManaBar.setWidth(((double)playerMana / maxMana) * 100);
        });

        try {
            objectOutputStream.writeObject(playerInformation);
            objectOutputStream.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    private void sendShootSignal() {
        if (isRpg) {
            playerMana -= rpgManaCost;
            playerInformation.setPlayerMana(playerMana);
            Platform.runLater(() -> playerManaLabel.setText(String.valueOf(playerMana)));

            if (playerMana < rpgManaCost)
                Platform.runLater(() -> rpgPermissionSign.setFill(Color.RED));

            if (pointerX >= enemyX&& pointerX <= enemyX + 150&& pointerY >= enemyY&& pointerY <= enemyY + 150) {
                try {
                    objectOutputStream.writeInt(rpgHpReduction);
                    objectOutputStream.flush();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        } else {

            if (pointerX >= enemyX&& pointerX <= enemyX + 150&& pointerY >= enemyY&& pointerY <= enemyY + 150) {
                try {
                    objectOutputStream.writeInt(bulletHpReduction);
                    objectOutputStream.flush();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    private void catchSignal() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                do {
                    try {
                        PlayerInformation enemyInformation = (PlayerInformation) objectInputStream.readObject();
                        enemyHp = enemyInformation.getPlayerHp();
                        enemyMana = enemyInformation.getPlayerMana();
                        enemyX = enemyInformation.getPlayerX();
                        enemyY = enemyInformation.getPlayerY();

                        int playerHpReduction = 0;
                        try {
                            playerHpReduction = objectInputStream.readInt();
                        } catch (Exception e) {

                        }
                        playerHp -= playerHpReduction;
                        playerInformation.setPlayerHp(playerHp);

                        Platform.runLater(() -> {
                            enemyImageView.setX(enemyX);
                            enemyImageView.setY(enemyY);
                            playerHpLabel.setText(String.valueOf(playerHp));
                            playerManaLabel.setText(String.valueOf(playerMana));
                            enemyHpLabel.setText(String.valueOf(enemyHp));
                            enemyManaLabel.setText(String.valueOf(enemyMana));

                            playerHpBar.setWidth(((double) playerHp / maxHp) * 100);
                            playerManaBar.setWidth(((double) playerMana / maxMana) * 100);
                            enemyHpBar.setWidth(((double) enemyHp / maxHp) * 100);
                            enemyManaBar.setWidth(((double) enemyMana / maxMana) * 100);

                            if (playerMana >= rpgManaCost)
                                rpgPermissionSign.setFill(Color.GREEN);
                        });

                    } catch (IOException e) {
//                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
                    }
                } while (playerHp > 0&& enemyHp > 0);
            }
        };
        thread.start();
    }

    private void setBoardListener() {

        scene.setOnMouseMoved(e -> {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        pointerImageView.setX(e.getX() - 25);
                        pointerImageView.setY(e.getY() - 25);
                    });
                }
            };
            thread.start();
        });


        scene.setOnMousePressed(e -> {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    pointerX = (int) e.getX();
                    pointerY = (int) e.getY();

                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        isRpg = false;

                        sendShootSignal();
                    }

                    else if (e.getButton().equals(MouseButton.SECONDARY)&& playerMana >= rpgManaCost) {
                        isRpg = true;

                        sendShootSignal();
                    }


                    scene.setOnMouseDragged(e -> {
                        Thread thread1 = new Thread() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    pointerImageView.setX(e.getX() - 25);
                                    pointerImageView.setY(e.getY() - 25);
                                });
                            }
                        };
                        thread1.start();
                    });
                }
            };

            thread.start();
        });


    }
}
