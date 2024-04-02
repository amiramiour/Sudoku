package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
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

        public Cellule(int valInitial, MainActivity context) {
            value = valInitial;
            if (value != 0) fixee = true;
            else fixee = false;
            bt = new Button(context);
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
                        checkWin();

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

    // Méthode pour générer une grille de Sudoku basée sur le niveau de difficulté choisi
    private String generateSudokuGrid(String[][] niveau) {
        Random random = new Random();
        int indexGrilleAleatoire = random.nextInt(niveau.length);

        // Accéder à la grille aléatoire
        String[] grille = niveau[indexGrilleAleatoire];

        StringBuilder gridBuilder = new StringBuilder();
        for (String ligne : grille) {
            gridBuilder.append(ligne).append("\n"); // Ajouter un saut de ligne après chaque ligne de la grille
        }
        return gridBuilder.toString();
    }


    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer le niveau de difficulté passé depuis l'intent
        int niveau = getIntent().getIntExtra("niveau", 0);

        // Choisissez la grille correspondant au niveau choisi
        String[][] grilleChoisie;
        switch (niveau) {
            case 1:
                grilleChoisie = GrillesSudoku.NIVEAU_FACILE;
                break;
            case 2:
                grilleChoisie = GrillesSudoku.NIVEAU_MOYEN;
                break;
            case 3:
                grilleChoisie = GrillesSudoku.NIVEAU_DIFFICILE;
                break;
            default:
                grilleChoisie = GrillesSudoku.NIVEAU_FACILE; // Par défaut, choisissez le niveau facile
        }

        // Générer la grille Sudoku
        String input = generateSudokuGrid(grilleChoisie);

        // Affichage de la grille dans la TableLayout
        afficherGrille(input);
    }

    // Méthode pour afficher la grille de Sudoku
    private void afficherGrille(String input) {
        tab = new Cellule[9][9];

        tl = findViewById(R.id.tableLayout); // Correctement référencer la TableLayout
        tl.setStretchAllColumns(true);
        tl.setShrinkAllColumns(true);
        String[] lignes = input.split("\n"); // Fractionner la grille en lignes

        for (int i = 0; i < 9; i++) {
            TableRow tr = new TableRow(this);
            String[] colonnes = lignes[i].split(" "); // Fractionner la ligne en colonnes en utilisant l'espace comme délimiteur

            for (int j = 0; j < 9; j++) {
                char c = colonnes[j].charAt(0);
                tab[i][j] = new Cellule(c == '?' ? 0 : Character.getNumericValue(c), this);
                tr.addView(tab[i][j].bt);
            }
            tl.addView(tr);
        }

        tv = findViewById(R.id.textView);

        linLay = findViewById(R.id.linLayout);
        linLay.setOrientation(LinearLayout.VERTICAL);
    }

    private void checkRowColumnCompletion() {
        // Vérifier les lignes
        for (int i = 0; i < 9; i++) {
            TableRow row = (TableRow) tl.getChildAt(i);
            if (isRowCorrectlyFilled(i)) {
                row.setBackgroundColor(Color.GREEN);
            } else {
                row.setBackgroundColor(Color.TRANSPARENT); // Remettre la couleur par défaut si la ligne n'est pas correctement remplie
            }
        }

        // Vérifier les colonnes
        for (int j = 0; j < 9; j++) {
            if (isColumnCorrectlyFilled(j)) {
                for (int i = 0; i < 9; i++) {
                    TableRow row = (TableRow) tl.getChildAt(i);
                    Button button = (Button) row.getChildAt(j);
                    button.setBackgroundColor(Color.GREEN);
                }
            } else {
                for (int i = 0; i < 9; i++) {
                    TableRow row = (TableRow) tl.getChildAt(i);
                    Button button = (Button) row.getChildAt(j);
                    button.setBackgroundColor(Color.TRANSPARENT); // Remettre la couleur par défaut si la colonne n'est pas correctement remplie
                }
            }
        }
    }

    private boolean isRowCorrectlyFilled(int rowIndex) {
        for (int j = 0; j < 9; j++) {
            if (tab[rowIndex][j].value == 0 || !correct(rowIndex, j, rowIndex + 1, j + 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnCorrectlyFilled(int colIndex) {
        for (int i = 0; i < 9; i++) {
            if (tab[i][colIndex].value == 0 || !correct(i, colIndex, i + 1, colIndex + 1)) {
                return false;
            }
        }
        return true;
    }
    public void resetGrid(View view) {
        // Parcourir toutes les cellules et réinitialiser leur valeur à 0 si elles ne sont pas fixes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!tab[i][j].fixee) {
                    tab[i][j].value = 0;
                    tab[i][j].bt.setText("");
                }
            }
        }

        // Remettre à jour l'affichage pour refléter les changements
        tv.setText(""); // Effacer tout message précédent
    }



    private boolean checkWin() {
        if (!complete() || !correct()) {
            return false; // Si la grille n'est pas complète ou correcte, le jeu n'est pas terminé
        }

        // Marquer les lignes et les colonnes correctement remplies
        checkRowColumnCompletion();

        // Afficher un message pour indiquer la fin du jeu
        tv.setTextColor(Color.GREEN); // Modifier la couleur du texte en vert
        tv.setText("Félicitations! Vous avez terminé la grille Sudoku!");

        // Vous pouvez également désactiver les boutons pour empêcher toute interaction supplémentaire

        return true;
    }
}
