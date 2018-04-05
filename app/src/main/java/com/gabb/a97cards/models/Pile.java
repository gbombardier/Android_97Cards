package com.gabb.a97cards.models;

import android.util.Log;

public class Pile {
    private int valeurCourante;
    private int sens;

    public Pile(int sens){
        this.valeurCourante = 0;
        this.sens =sens;

        if(sens == 0){
            this.valeurCourante = 0;
        }else{
            this.valeurCourante = 97;
        }
    }

    public boolean ajouterCarte(int val) {
        Log.d("val", Integer.toString(valeurCourante));
        boolean fonctionne;
        if (this.sens == 0) {
            fonctionne = ajouterCarteCroissante(val);
        } else {
            fonctionne = ajouterCarteDecroissante(val);
        }

        return fonctionne;
    }

    private boolean ajouterCarteCroissante(int val) {
        Log.d("ici", "NON");
        if(val > valeurCourante || val - valeurCourante == -10){
            this.valeurCourante = val;
            return true;
        }else{
            return false;
        }
    }

    private boolean ajouterCarteDecroissante(int val) {
        if(val < valeurCourante || valeurCourante - val == -10){
            this.valeurCourante = val;
            return true;
        }else{
            return false;
        }
    }

    public int getValeurCourante() {
        return valeurCourante;
    }
}
