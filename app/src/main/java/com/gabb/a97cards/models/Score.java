package com.gabb.a97cards.models;

import java.io.Serializable;

public class Score implements Serializable{
    private int score;

    public Score(){
        this.score = 0;
    }

    public void ajouterPoints(int val){
        this.score+=val;
    }

    public int getScore() {
        return score;
    }
}
