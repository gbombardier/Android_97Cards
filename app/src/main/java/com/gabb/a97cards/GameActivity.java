package com.gabb.a97cards;

import android.content.ClipData;
import android.content.Context;
import android.sax.TextElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gabb.a97cards.models.Carte;
import com.gabb.a97cards.models.Pile;
import com.gabb.a97cards.models.Score;
import com.gabb.a97cards.models.Suite;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import static android.view.DragEvent.ACTION_DRAG_ENDED;
import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DROP;

public class GameActivity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener {

    private TextView viewCartesRest, viewChrono, viewPoints;
    private Vector<Pile> piles = new Vector<Pile>();
    private Vector<TextView> viewPiles = new Vector<TextView>();
    private Vector<TextView> viewCartes = new Vector<TextView>();
    private Vector<LinearLayout> layoutsPiles = new Vector<LinearLayout>();
    private Vector<LinearLayout> layoutsCartes = new Vector<LinearLayout>();
    private Suite cartesJoueur = new Suite();
    private Score score = new Score();
    private int nbCartesReste = 97, nbCartesDone = 0, nbCartesJeu = 8;
    private int seconds = 0, minutes = 0;
    private Timer t;
    private Vector<TextView> espacesTemp = new Vector<TextView>();
    private LinearLayout mainLayout;
    FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Pour lier les views
        viewCartesRest = (TextView) findViewById(R.id.view_cartes_restantes);
        viewChrono = (TextView) findViewById(R.id.view_chrono);
        viewPoints = (TextView) findViewById(R.id.view_points);

        viewPiles.add((TextView) findViewById(R.id.val_pile1));
        viewPiles.add((TextView) findViewById(R.id.val_pile2));
        viewPiles.add((TextView) findViewById(R.id.val_pile3));
        viewPiles.add((TextView) findViewById(R.id.val_pile4));

        viewCartes.add((TextView) findViewById(R.id.val_carte1));
        viewCartes.add((TextView) findViewById(R.id.val_carte2));
        viewCartes.add((TextView) findViewById(R.id.val_carte3));
        viewCartes.add((TextView) findViewById(R.id.val_carte4));
        viewCartes.add((TextView) findViewById(R.id.val_carte5));
        viewCartes.add((TextView) findViewById(R.id.val_carte6));
        viewCartes.add((TextView) findViewById(R.id.val_carte7));
        viewCartes.add((TextView) findViewById(R.id.val_carte8));

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);


        layoutsCartes.add((LinearLayout) findViewById(R.id.layout_cartes1));
        layoutsCartes.add((LinearLayout) findViewById(R.id.layout_cartes2));

        layoutsPiles.add((LinearLayout) findViewById(R.id.layout_pile1));
        layoutsPiles.add((LinearLayout) findViewById(R.id.layout_pile2));
        layoutsPiles.add((LinearLayout) findViewById(R.id.layout_pile3));
        layoutsPiles.add((LinearLayout) findViewById(R.id.layout_pile4));

        piles.add(new Pile(1));
        piles.add(new Pile(1));
        piles.add(new Pile(0));
        piles.add(new Pile(0));

        //Créé un timer
        ajouterTimer();

        //Ajoute les écouteurs
        for(TextView pile : viewPiles){
            pile.setOnDragListener(this);
        }
        mainLayout.setOnDragListener(this);

        //Initialise les cartes, piles et le score
        initialiserCartes();
        initialiserPiles();
        viewPoints.setText("Score: " + score.getScore());


    }

    public void ajouterTimer(){
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seconds += 1;
                        if(seconds<=9){
                            viewChrono.setText("Temps: "+ String.valueOf(minutes)+":0"+String.valueOf(seconds));
                        }else if(seconds >=10 && seconds<=59){
                            viewChrono.setText("Temps: "+ String.valueOf(minutes)+":"+String.valueOf(seconds));
                        }else if(seconds == 60){
                            seconds=0;
                            minutes=minutes+1;
                            viewChrono.setText("Temps: "+ String.valueOf(minutes)+":0"+String.valueOf(seconds));
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    //Permet d'enregistrer le score lorsque l'on quitte l'application. SEULEMENT LE SCORE
    @Override
    protected void onStop(){
        super.onStop();
        sauvegarderObjet(score);
    }

    //Écris dans la BD interne pour enregistrer le score courant
    public void sauvegarderObjet(Score score){
        Bundle extras = getIntent().getExtras();
        int highScore = extras.getInt("highScore");

        if(score.getScore() > highScore){
            try{
                fos = openFileOutput("fichier.ser", Context.MODE_PRIVATE); //Flux de communication
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(score);
                oos.flush();
                oos.close();
            }catch(Exception ex){
                Log.v("erreur: ", ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    //Pour lier les textviews et les cartes
    public void initialiserCartes(){
        for(TextView view : viewCartes){
            view.setOnTouchListener(this);
            int val = cartesJoueur.get(nbCartesDone);
            view.setText(Integer.toString(val));
            nbCartesDone++;
        }
        viewCartesRest.setText("Cartes: " + (97-nbCartesDone));
    }

    //Pour lier les textviews et les piles
    public void initialiserPiles(){

        viewPiles.get(0).setText(Integer.toString(piles.get(0).getValeurCourante()));
        viewPiles.get(1).setText(Integer.toString(piles.get(1).getValeurCourante()));
        viewPiles.get(2).setText(Integer.toString(piles.get(2).getValeurCourante()));
        viewPiles.get(3).setText(Integer.toString(piles.get(3).getValeurCourante()));
    }

    //Pour enregistrer le score
    @Override
    public void onSaveInstanceState(Bundle infos){
        super.onSaveInstanceState(infos);
        infos.putSerializable("score", score);
    }


    //Pour vérifier si on peut encore bouger
    public boolean verifierPossibilites(){
        boolean possible = false;

        for(TextView carte : viewCartes){
            int valCourante = Integer.parseInt(carte.getText().toString());

            if(valCourante < Integer.parseInt(viewPiles.get(0).getText().toString()) || Integer.parseInt(viewPiles.get(0).getText().toString()) - valCourante == -10){
                possible = true;
            }else if(valCourante < Integer.parseInt(viewPiles.get(1).getText().toString()) || Integer.parseInt(viewPiles.get(1).getText().toString()) - valCourante == -10){
                possible = true;
            }else if(valCourante > Integer.parseInt(viewPiles.get(2).getText().toString()) || Integer.parseInt(viewPiles.get(2).getText().toString()) - valCourante == 10){
                possible = true;
            }else if(valCourante > Integer.parseInt(viewPiles.get(3).getText().toString()) || Integer.parseInt(viewPiles.get(3).getText().toString()) - valCourante == 10){
                possible = true;
            }
        }
        if(possible == false){
            Toast.makeText(getApplicationContext(),"FIN DU JEU", Toast.LENGTH_SHORT).show();
        }
        return possible;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        Log.d("event: ", "touch");
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        Log.d("event: ", "drag");
        switch(dragEvent.getAction()){
            case(ACTION_DRAG_ENTERED):
                break;
            case(ACTION_DRAG_EXITED):
                break;
            case(ACTION_DROP):
                View view = (View) dragEvent.getLocalState();
                TextView dropped = (TextView) view;

                try{
                    TextView target = (TextView) v;
                    if(target instanceof TextView){
                        boolean valide = actionPile(target, dropped);
                        if(!valide){
                            view.setVisibility(View.VISIBLE);
                        }
                    }else{
                        view.setVisibility(View.VISIBLE);
                    }
                }catch(Exception ex){
                    view.setVisibility(View.VISIBLE);
                }

                break;
            case(ACTION_DRAG_ENDED):
                break;
        }
        return true;
    }

    //Pour ajouter a une pile et gère le fait de remettre des cartes en jeu
    public boolean actionPile(TextView valeurPile, TextView carte){

        String id = getResources().getResourceName(valeurPile.getId());
        int rang = Integer.parseInt(id.substring(id.length()-1));

        boolean actionResult = piles.get(rang-1).ajouterCarte(Integer.parseInt(carte.getText().toString()));

        if(actionResult){
            valeurPile.setText(Integer.toString(piles.get(rang-1).getValeurCourante()));
            score.ajouterPoints(Integer.parseInt(carte.getText().toString()));
            viewPoints.setText("Score: " + score.getScore());
            espacesTemp.add(carte);

            nbCartesJeu--;
            Log.d("nbCartesJeu", Integer.toString(nbCartesJeu));
            if(nbCartesJeu == 6){
                String idCarte1 = getResources().getResourceName(espacesTemp.get(0).getId());
                int rangCarte1 = Integer.parseInt(idCarte1.substring(idCarte1.length()-1));

                String idCarte2 = getResources().getResourceName(espacesTemp.get(1).getId());
                int rangCarte2 = Integer.parseInt(idCarte2.substring(idCarte2.length()-1));

                TextView aRemettre1 = (TextView) findViewById(espacesTemp.get(0).getId());
                TextView aRemettre2 = (TextView) findViewById(espacesTemp.get(1).getId());

                nbCartesDone++;
                aRemettre1.setText(Integer.toString(cartesJoueur.get(nbCartesDone)));
                aRemettre1.setVisibility(View.VISIBLE);
                nbCartesDone++;
                aRemettre2.setText(Integer.toString(cartesJoueur.get(nbCartesDone)));
                aRemettre2.setVisibility(View.VISIBLE);
                nbCartesJeu = 8;
                viewCartesRest.setText("Cartes: " + (97-nbCartesDone));

                espacesTemp.clear();
            }

            verifierPossibilites();
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"ÇA C'EST NON!!!!!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
