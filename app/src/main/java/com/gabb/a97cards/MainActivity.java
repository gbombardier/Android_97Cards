package com.gabb.a97cards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gabb.a97cards.models.Score;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button playButton;
    private Score score;
    private TextView viewPoints;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        viewPoints = (TextView) findViewById(R.id.high_score_view);

        //Pour récupérer le high score
        try{
            score = recupererObjetSerialise();
            highScore = score.getScore();
            viewPoints.setText("High Score: " + score.getScore());
        }catch (Exception ex){
            Log.v("erreur: ", ex.getMessage());
            ex.printStackTrace();

            if(savedInstanceState !=null && (savedInstanceState.getSerializable("utilisateur"))!=null){
                score = (Score) savedInstanceState.getSerializable("score");
                highScore = score.getScore();
                viewPoints.setText("High Score: " + score.getScore());
            }else{
                highScore = 0;
                viewPoints.setText("High Score: Pas encore calculé!");
            }
        }
    }

    //Change le intent et commence le jeu
    @Override
    public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, GameActivity.class);
        i.putExtra("highScore", highScore);
        startActivity(i);
    }

    //Permet de récuperer le score s'il a été sérialisé
    public Score recupererObjetSerialise(){
        Score score = null;
        try{
            FileInputStream fis = openFileInput("fichier.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            score = (Score)ois.readObject();
            ois.close();
        }catch (Exception ex){
            Log.v("erreur: ", ex.getMessage());
            ex.printStackTrace();
        }

        return score;
    }
}
