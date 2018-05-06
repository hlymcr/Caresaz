package com.hsn.caresaz.caresaz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.hsn.caresaz.caresaz.ProfilResimtasarim.RoundedTransformationBuilder;
import com.hsn.caresaz.caresaz.model.Cihaz;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Map;

public class CihazEkle extends AppCompatActivity {
    private ImageView yardim, resim;
    private EditText kod, isim;
    private Spinner tur;
    private String cihaztur;
    private ArrayAdapter<String> Turler;
    private Button ekle;
    private String[] turler = {"SEÇİNİZ","Tasma", "Bileklik"};
    private static final int RESIM_ISTEK = 123;
    private Uri dosyaYolu;
    private String kullaniciID;
    private Cihaz cihaz;
    private DatabaseReference mVeritabani;
    private Map<String, Object> postValues;
    private FirebaseAuth mKullanici;
    private FirebaseStorage firebaseVeritabani;
    private ProgressDialog cihazEkleDiyalog;

    final Transformation ceviri = new RoundedTransformationBuilder()
            .borderColor(Color.GRAY)
            .borderWidthDp(4)
            .cornerRadiusDp(35)
            .oval(true)
            .build();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cihaz_ekle);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.cihaz_ekle);

        yardim = (ImageView) findViewById(R.id.yardim);
        resim = (ImageView) findViewById(R.id.cihazresim);
        kod = (EditText) findViewById(R.id.kod);
        isim = (EditText) findViewById(R.id.isim);
        tur = (Spinner) findViewById(R.id.spinner);
        ekle = (Button) findViewById(R.id.ekle);
        cihaz = new Cihaz();

        Turler = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, turler);
        Turler.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tur.setAdapter(Turler);

        mKullanici = FirebaseAuth.getInstance();
        firebaseVeritabani = FirebaseStorage.getInstance();


        tur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                cihaztur = tur.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yardim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yardimDialog();
            }
        });

        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), RESIM_ISTEK);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        kullaniciID = user.getUid();

        mVeritabani = FirebaseDatabase.getInstance().getReference();

        //Ekle metodu cihaz aktivasyon kodlarına göre daha sonra düzenlenecektir.
        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cihaz.setIsim(isim.getText().toString());
                cihaz.setTur(cihaztur);
                cihaz.setKod(kod.getText().toString());
                cihaz.setResim(String.valueOf(dosyaYolu));
                //mDatabase.child("cihazlar").child(userID).setValue(cihaz);
                //Toast.makeText(CihazEkle.this, "Kaydedildi", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.lin1), "Aktivasyon kodu yanlıştır.", Snackbar.LENGTH_LONG);
                snackbar.show();


            }


        });


    }

    public void yardimDialog() {
        // dialog nesnesi oluştur ve layout dosyasına bağlan
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_cihazyardim_dialog);
        Button tamam = (Button) dialog.findViewById(R.id.tamam);
        TextView kod = (TextView) dialog.findViewById(R.id.kod);
        TextView resim = (TextView) dialog.findViewById(R.id.resim);
        TextView isim = (TextView) dialog.findViewById(R.id.isim);
        TextView tur = (TextView) dialog.findViewById(R.id.tur);

        kod.setText(R.string.kod_yardim);
        resim.setText(R.string.resim_yardim);
        isim.setText(R.string.isim_yardim);
        tur.setText(R.string.tur_yardim);
        dialog.show();

        tamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESIM_ISTEK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            dosyaYolu = data.getData();
            try {
                Picasso.with(CihazEkle.this).load(dosyaYolu).fit().transform(ceviri).into(resim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
