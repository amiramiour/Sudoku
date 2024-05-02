package com.example.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class loadingactivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        // Utiliser un Handler pour retarder le démarrage de MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Créer une intention pour passer à MainActivity
                Intent intent = new Intent(loadingactivity.this, StartPageActivity.class);
                startActivity(intent);
                // Terminer l'activité de chargement pour éviter de revenir en arrière
                finish();
            }
        }, DELAY_MILLIS);
    }
}
