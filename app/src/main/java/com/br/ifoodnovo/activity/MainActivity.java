package com.br.ifoodnovo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.br.ifoodnovo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAuntenticacao();
            }
        },3000);
    }

    private void abrirAuntenticacao(){
        Intent abrir = new Intent(MainActivity.this, AutenticacaoActivity.class);
        startActivity(abrir);
        finish();
    }
}