/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraza2;

import at.falb.games.alcatraz.api.*;
import java.io.Serializable;

/**
 *
 * @author alexa
 */
public class Move implements Serializable {

    private int rowOrCol;
    private int row;
    private int col;
    private Player player;
    private Prisoner prisoner;

    public Move() {
    }

    public Move(Player pl, Prisoner pr, int rowOrCol, int row, int col) {
        super();
        this.player = pl;
        this.prisoner = pr;
        this.rowOrCol = rowOrCol;
        this.row = row;
        this.col = col;
    }

    public void setRowOrCol(int roc) {
        this.rowOrCol = roc;
    }

    public void setRow(int r) {
        this.row = r;
    }

    public void setCol(int c) {
        this.col = c;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    public void setPrisoner(Prisoner p) {
        this.prisoner = p;
    }

    public int getRowOrCol() {
        return this.rowOrCol;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Prisoner getPrisoner() {
        return this.prisoner;
    }
}
