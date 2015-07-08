package gui;

import java.io.Serializable;

/**
 * Created by Szamani on 7/7/2015.
 */
public class PlayerInformation implements Serializable {
    private int playerHp;
    private int playerMana;
    private int playerX;
    private int playerY;
    public static final long serialVersionUID = 123456789;

    public PlayerInformation(int playerHp, int playerMana, int playerX, int playerY) {
        this.playerHp = playerHp;
        this.playerMana = playerMana;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = playerHp;
    }

    public int getPlayerMana() {
        return playerMana;
    }

    public void setPlayerMana(int playerMana) {
        this.playerMana = playerMana;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }
}
