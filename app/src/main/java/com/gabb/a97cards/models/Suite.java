package com.gabb.a97cards.models;

import java.util.Collections;
import java.util.Vector;

public class Suite {

    private Vector<Carte> suiteCarte;

    public Suite(){
        suiteCarte = new Vector<Carte>();
        initialiserSuite();
    }

    public Vector<Carte> getSuiteCarte() {
        return suiteCarte;
    }

    public void initialiserSuite(){
        for(int i = 1; i<97;i++){
            Carte temp = new Carte(i);
            suiteCarte.add(temp);
        }

       Collections.shuffle(suiteCarte);
    }

    public int get(int position){
        return suiteCarte.get(position).getValeur();
    }
}
