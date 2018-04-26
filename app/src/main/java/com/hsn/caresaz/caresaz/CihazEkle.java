package com.hsn.caresaz.caresaz;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hsn.caresaz.caresaz.model.Cihaz;
import com.hsn.caresaz.caresaz.model.KullaniciModel;
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
    private String[] turler = {"Tasma", "Bileklik"};
    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;
    private String userID;
    private Cihaz cihaz;
    private DatabaseReference mDatabase;
    private Map<String, Object> postValues;
    private FirebaseAuth mAuth;
    private FirebaseStorage fStorage;
    private ProgressDialog progressDialog;

    final Transformation transformation = new RoundedTransformationBuilder()
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

        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();


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
                showMyCustomAlertDialog();
            }
        });

        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), PICK_IMAGE_REQUEST);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        Log.d("userID:", userID);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Cihaz aktivasyon koduna göre düzenlenecektir.
        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cihaz.setIsim(isim.getText().toString());
                cihaz.setTur(cihaztur);
                cihaz.setKod(kod.getText().toString());
                cihaz.setResim(String.valueOf(filePath));
                //mDatabase.child("cihazlar").child(userID).setValue(cihaz);
                Toast.makeText(CihazEkle.this, "Kaydedildi", Toast.LENGTH_SHORT).show();
                Log.i("SAVE", "saveEntry: Kaydedildi.");
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.lin1), "Aktivasyon kodu yanlıştır.", Snackbar.LENGTH_LONG);
                snackbar.show();

               /* if (filePath != null) {
                    progressDialog = new ProgressDialog(CihazEkle.this);
                    progressDialog.setMessage("Yükleniyor...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StorageReference storageRef = fStorage.getReference().child("cihazresim").child(mAuth.getCurrentUser().getUid()).child(kod.getText().toString());
                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            //Toast.makeText(ProfilDuzenle.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();
                            resim.setImageBitmap(null);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(CihazEkle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }*/


            }


        });


    }

    public void showMyCustomAlertDialog() {
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Picasso.with(CihazEkle.this).load(filePath).fit().transform(transformation).into(resim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
