package com.br.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.br.ifoodclone.R;

public class RecuperaSenhaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);
    }

    public void voltarLogin(View View){
        startActivity(new Intent(getApplicationContext(),AutenticacaoActivity.class));
    }
}