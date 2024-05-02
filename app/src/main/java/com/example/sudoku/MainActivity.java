package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.Random;
import java.util.Stack;
import com.airbnb.lottie.LottieAnimationView;


public class MainActivity extends AppCompatActivity {

    private Cellule selectedCell;

    private class Cellule {

        int value;
        boolean fixee;
        int rowIndex;
        int columnIndex;
        Button bt;
        public void selectNumber(int selectedNumber) {
            // Vérifier si le timer est en pause
            if (isTimerPaused) {
                // Afficher un message indiquant que le jeu est en pause
                Toast.makeText(MainActivity.this, "Le jeu est en pause. Revenez pour reprendre le jeu.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier si la cellule est fixée
            if (fixee) return;

            // Insérer le chiffre sélectionné dans la cellule
            value = selectedNumber;
            undoStack.push(Cellule.this);

            bt.setText(String.valueOf(value));
            if (correct()) {
                tv.setText("");
                checkWin();
            } else {
                tv.setText("");
            }
            updateTextColor();
        }
        private int lastClickedRow = -1;
        private int lastClickedColumn = -1;
        public Cellule(int valInitial, MainActivity context, int rowIndex, int columnIndex) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            value = valInitial;
            fixee = value != 0;
            bt = new Button(context);
            if (fixee) {
                bt.setText(String.valueOf(value));
                bt.setTextColor(Color.BLACK); // Les chiffres fixes sont en noir
            } else {
                bt.setTextColor(Color.RED); // Les chiffres non-fixes sont en rouge
            }
            bt.setOnClickListener(v -> {

                // Si le jeu est en pause, ne faites rien
                if (isTimerPaused) {
                    return;
                }

                // Réinitialiser la couleur de toutes les cellules
                resetColors();

                // Colorer la ligne et la colonne
                colorRowAndColumn();
                // Si la cellule n'est pas fixée, la sélectionner
                if (!fixee) {
                    selectedCell = this; // 'this' fait référence à l'objet Cellule actuel
                }

            });


        }


        private void resetColors() {
            for (int i = 0; i < 9; i++) {
                TableRow row = (TableRow) tl.getChildAt(i);
                for (int j = 0; j < 9; j++) {
                    Button button = (Button) row.getChildAt(j);
                    button.setBackgroundColor(Color.TRANSPARENT);  // Couleur par défaut
                }
            }
        }

        private void colorRowAndColumn() {
            // Colorer la ligne
            TableRow selectedRow = (TableRow) tl.getChildAt(rowIndex);
            for (int i = 0; i < selectedRow.getChildCount(); i++) {
                selectedRow.getChildAt(i).setBackgroundColor(Color.LTGRAY);
            }

            // Colorer la colonne
            for (int i = 0; i < 9; i++) {
                TableRow row = (TableRow) tl.getChildAt(i);
                row.getChildAt(columnIndex).setBackgroundColor(Color.LTGRAY);
            }
            int color = ContextCompat.getColor(bt.getContext(), R.color.light_blue); // Récupération de la couleur bleue claire

            bt.setBackgroundColor(color);

        }


        private void updateTextColor() {
            if (correct()) {
                bt.setTextColor(Color.GREEN); // Si le chiffre n'est pas répété, le rendre noir
            } else {
                bt.setTextColor(Color.RED); // Si le chiffre est répété, le rendre rouge
            }
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
    Stack<Cellule> undoStack; // Déclarer une pile pour enregistrer l'historique des actions de l'utilisateur
    private LottieAnimationView animationView; // Déclaration de la variable pour stocker la référence de l'animation


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
        timerTextView = findViewById(R.id.timerTextView);
        pauseResumeButton = findViewById(R.id.pauseResumeButton);

        // Démarrez le timer lorsque l'activité est créée
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        undoStack = new Stack<>();

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

        assignNumberButtonListeners();

        // Affichage de la grille dans la TableLayout
        afficherGrille(input);
    }
    private void assignNumberButtonListeners() {
        LinearLayout numbersLayoutFirstRow = findViewById(R.id.firstRowLinearLayout);
        LinearLayout numbersLayoutSecondRow = findViewById(R.id.secondRowLinearLayout);

        // Supposons que vous avez deux LinearLayouts pour les boutons, comme mentionné.
        // Sinon, ajustez ce code pour correspondre à votre structure de mise en page.

        for (int i = 1; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("number" + i, "id", getPackageName());
            Button numberButton = findViewById(buttonId);
            final int finalI = i; // Nécessaire pour être utilisé dans le lambda
            numberButton.setOnClickListener(v -> {
                if (selectedCell != null && !selectedCell.fixee) {
                    selectedCell.selectNumber(finalI);
                    selectedCell = null; // Désélectionner la cellule après l'insertion
                }
            });

            // Ceci assigne un écouteur à chaque bouton numérique.
            // Assurez-vous que les IDs dans votre fichier XML correspondent (number1, number2, ..., number9).
        }
    }
    // Méthode pour afficher la grille de Sudoku
    private void afficherGrille(String input) {
        tab = new Cellule[9][9];

        tl = findViewById(R.id.tableLayout);
        tl.setStretchAllColumns(true);
        tl.setShrinkAllColumns(true);
        String[] lignes = input.split("\n");

        for (int i = 0; i < 9; i++) {
            TableRow tr = new TableRow(this);
            String[] colonnes = lignes[i].split(" ");

            for (int j = 0; j < 9; j++) {
                char c = colonnes[j].charAt(0);
                tab[i][j] = new Cellule(c == '?' ? 0 : Character.getNumericValue(c), this, i, j);
                tr.addView(tab[i][j].bt);
                tab[i][j].bt.setBackgroundColor(Color.TRANSPARENT);
                tab[i][j].bt.setTag(tab[i][j]);
            }
            tl.addView(tr);
        }

        LinearLayout numbersLayout = findViewById(R.id.numbersLayout);
       /* for (int i = 1; i <= 9; i++) {
            Button numberButton = new Button(this);
            numberButton.setText(String.valueOf(i));
            numberButton.setOnClickListener(v -> {
                int selectedNumber = Integer.parseInt(((Button) v).getText().toString());
                if (selectedCell != null && !selectedCell.fixee) {
                    selectedCell.selectNumber(selectedNumber);
                    selectedCell = null; // Reset the selected cell after insertion
                }
            });
            numbersLayout.addView(numberButton);
        }*/

        tv = findViewById(R.id.textView);
        linLay = findViewById(R.id.linLayout);
        linLay.setOrientation(LinearLayout.VERTICAL);
    }




    private void checkRowColumnCompletion() {
        // Vérifier les lignes
        for (int i = 0; i < 9; i++) {
            TableRow row = (TableRow) tl.getChildAt(i);
            if (isRowCorrectlyFilled(i)) {
                //row.setBackgroundColor(Color.GREEN);
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
                    //button.setBackgroundColor(Color.GREEN);
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
    public void toggleTimerPauseResume(View view) {
        if (isTimerPaused) {
            // Reprendre le timer
            isTimerPaused = false;
            pauseResumeButton.setText("Pause");
            startTime = System.currentTimeMillis() - pausedTime;
            timerHandler.postDelayed(timerRunnable, 0);
            // Fermer le dialogue de flou si ouvert
            if (blurDialog != null && blurDialog.isShowing()) {
                blurDialog.dismiss();
            }
        } else {
            // Mettre en pause le timer
            isTimerPaused = true;
            pausedTime = System.currentTimeMillis() - startTime;
            timerHandler.removeCallbacks(timerRunnable);
            // Afficher le dialogue de flou
            showBlurDialog();
        }
    }

    private Dialog blurDialog; // Ajoutez ceci en haut de votre classe MainActivity

    private void showBlurDialog() {
        blurDialog = new Dialog(this);
        blurDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        blurDialog.setContentView(R.layout.dialog_blur);
        blurDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        blurDialog.setCancelable(false); // pour empêcher la fermeture du dialogue lors du toucher à l'extérieur

        ImageButton resumeButton = blurDialog.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(v -> toggleTimerPauseResume(null));

        blurDialog.show();
    }

    public void deleteNumber(View view) {
        // Vérifiez si une cellule est sélectionnée et que celle-ci n'est pas fixée
        if (selectedCell != null && !selectedCell.fixee) {
            // Videz la cellule
            selectedCell.value = 0;
            selectedCell.bt.setText("");
            selectedCell = null; // Désélectionnez la cellule après la suppression
            // Vous pouvez également vouloir mettre à jour l'affichage ou le statut de la grille ici
        }
    }


    public void resetGrid(View view) {
        startTime = System.currentTimeMillis();

        // Parcourir toutes les cellules et réinitialiser leur valeur à 0 si elles ne sont pas fixes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!tab[i][j].fixee) {
                    tab[i][j].value = 0;
                    tab[i][j].bt.setText("");
                }
            }
        }

        // Supprimer les animations des feux d'artifice s'il y en a
        removeFireworksAnimations();



        // Remettre à jour l'affichage pour refléter les changements
        tv.setText(""); // Effacer tout message précédent
    }
    private void removeFireworksAnimations() {
        for (int i = 0; i < linLay.getChildCount(); i++) {
            View child = linLay.getChildAt(i);
            if (child instanceof LottieAnimationView) {
                linLay.removeViewAt(i);
                break; // Sortir de la boucle après avoir supprimé la première animation des feux d'artifice
            }
        }
    }



    private boolean checkWin() {
        if (!complete() || !correct()) {
            return false; // Si la grille n'est pas complète ou correcte, le jeu n'est pas terminé
        }

        // Marquer les lignes et les colonnes correctement remplies
        checkRowColumnCompletion();
        timerHandler.removeCallbacks(timerRunnable);

        // Cacher le TextView habituel
        tv.setVisibility(View.INVISIBLE);

        // Créer une vue d'animation Lottie
        LottieAnimationView animationView = new LottieAnimationView(this);
        animationView.setAnimation(R.raw.fireworks); // Chemin de l'animation JSON de feu d'artifice dans les ressources
        animationView.loop(true); // Boucler l'animation indéfiniment
        animationView.playAnimation(); // Démarrer l'animation


        // Définir la taille de l'animation (par exemple, 400x400 pixels)
        int size = 2000; // Taille souhaitée des feux d'artifice
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        animationView.setLayoutParams(layoutParams);

        // Ajouter la vue d'animation à votre mise en page
        linLay.addView(animationView);

        // Mettre à jour le fond de votre mise en page pour le rendre noir (pour un meilleur contraste avec l'animation)
        // Ajuster les valeurs x et y des feux d'artifice
        float translationX = -50; // Décalage horizontal des feux d'artifice
        float translationY = -1000; // Décalage vertical des feux d'artifice
        animationView.setTranslationX(translationX);
        animationView.setTranslationY(translationY);


        // Afficher le message "You Win" au centre de l'écran
        WinDialogFragment dialog = new WinDialogFragment();
        dialog.show(this.getSupportFragmentManager(), "WinDialogFragment");


        return true;
    }

    public void undoChiffre(View view) {
        // Vérifier si la pile n'est pas vide
        while (!undoStack.isEmpty()) {
            // Obtenir la dernière action de la pile
            Cellule cellule = undoStack.peek(); // Obtenir l'élément en haut de la pile sans le retirer
            // Vérifier si la cellule est déjà réinitialisée
            if (cellule.value == 0) {
                // Si la cellule est déjà réinitialisée, retirer l'action de la pile
                undoStack.pop();
            } else {
                // Réinitialiser la valeur de la cellule à zéro
                cellule.value = 0;
                // Mettre à jour le texte de la cellule
                cellule.bt.setText("");
                // Arrêter la boucle car une action a été annulée
                break;
            }
        }
    }
    private TextView timerTextView;
    private Button pauseResumeButton;
    private boolean isTimerPaused = false;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerPaused) {
                // Si le timer est en pause, ne pas mettre à jour l'interface utilisateur
                return;
            }
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };



    // Déclarez la variable pour stocker le temps de pause du timer
    private long pausedTime = 0;


}