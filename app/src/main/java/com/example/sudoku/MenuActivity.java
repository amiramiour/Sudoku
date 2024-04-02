package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private int niveau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);

        Button buttonFacile = findViewById(R.id.button_facile);
        Button buttonMoyen = findViewById(R.id.button_moyen);
        Button buttonDifficile = findViewById(R.id.button_difficile);

        buttonFacile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Choisissez une grille spécifique du niveau facile
                niveau = 1;
                startMainActivity();
            }
        });

        buttonMoyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Choisissez une grille spécifique du niveau moyen
                niveau = 2;
                startMainActivity();
            }
        });

        buttonDifficile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Choisissez une grille spécifique du niveau difficile
                niveau = 3;
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("niveau", niveau);
        startActivity(intent);
    }
}
