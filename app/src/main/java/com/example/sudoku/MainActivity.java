package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private class Cellule {
        int value;
        boolean fixee;
        Button bt;

        public Cellule(int valInitial, Context THIS) {
            value = valInitial;
            if (value != 0) fixee = true;
            else fixee = false;
            bt = new Button(THIS);
            if (fixee) bt.setText(String.valueOf(value));
            else bt.setTextColor(Color.RED);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fixee) return;
                    value++;
                    if (value > 9) value = 1;
                    bt.setText(String.valueOf(value));
                    if (correct()) {
                        tv.setText("");
                    } else {
                        tv.setText("There's a repeated digit");
                    }
                }
            });
        }
    }

    boolean complete(){
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if (tab[i][j].value==0)
                    return false;
            }
        }
        return true;
    }
    boolean correct(int i1,int j1,int i2,int j2)
    {
        boolean[] vu=new boolean[10];
        for(int i=0;i<9;i++) vu[i]=false;
        for(int i=i1;i<i2;i++)
        {
            for(int j=j1;j<j2;j++)
            {
                int valeur=tab[i][j].value;
                if(valeur !=0) {
                    if(vu[valeur]) return false;
                    vu[valeur]=true;
                }
            }
        }
        return true;

    }
    boolean correct(){
        for(int i=0;i<9;i++)
            if(!correct(i,0,i+1,9)) return false ;
        for(int j=0;j<9;j++)
            if(!correct(0,j,9,j+1)) return false;
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(!correct(3*i,3*j,3*i+3,3*j+3))
                    return false;

        return true;

    }

    Cellule[][] tab;
    TableLayout tl;

    TextView tv;
    LinearLayout linLay;

    // Ajout de la variable input pour stocker la grille de Sudoku
    String input;

    // Méthode pour générer une grille de Sudoku aléatoire basée sur le niveau de difficulté choisi
    private String generateRandomSudokuInput(String[][] niveau) {
        StringBuilder inputBuilder = new StringBuilder();

        Random random = new Random();
        String[] grille = niveau[random.nextInt(niveau.length)];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                inputBuilder.append(grille[i].charAt(j)).append(" ");
            }
        }

        return inputBuilder.toString().trim();
    }

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer le niveau de difficulté passé depuis l'intent
        int niveau = (int) getIntent().getSerializableExtra("niveau");



        // Générer une nouvelle grille de Sudoku aléatoire pour le niveau de difficulté choisi
        String input = generateRandomSudokuInput(niveau);

        // Extracting values correctly from the input
        String[] lines = input.split("\n");
        tab = new Cellule[9][9];

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);
        for (int i = 0; i < 9; i++) {
            TableRow tr = new TableRow(this);

            String[] values = lines[i].split(" ");
            for (int j = 0; j < 9; j++) {
                String s = values[j];
                Character c = s.charAt(0);
                tab[i][j] = new Cellule(c == '?' ? 0 : Character.getNumericValue(c), this);
                tr.addView(tab[i][j].bt);
            }
            tableLayout.addView(tr);
        }

        tv = findViewById(R.id.textView);

        linLay = findViewById(R.id.linLayout);
        linLay.setOrientation(LinearLayout.VERTICAL);
    }

}
