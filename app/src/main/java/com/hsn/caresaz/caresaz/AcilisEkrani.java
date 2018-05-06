package com.hsn.caresaz.caresaz;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class AcilisEkrani extends Activity {

   //Süre tanımlama
    private static int Acilma_suresi = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_ekrani);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(AcilisEkrani.this, GirisKayit.class);
                startActivity(i);
                finish();

            }
        }, Acilma_suresi);
    }
}