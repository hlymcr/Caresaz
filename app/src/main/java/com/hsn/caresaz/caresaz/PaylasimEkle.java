package com.hsn.caresaz.caresaz;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hsn.caresaz.caresaz.ProfilResimtasarim.RoundedTransformationBuilder;
import com.hsn.caresaz.caresaz.model.KullaniciModel;
import com.hsn.caresaz.caresaz.model.PaylasmaModel;
import com.hsn.caresaz.caresaz.model.SehirIlceModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PaylasimEkle extends AppCompatActivity {

    Spinner spinnerKonu;
    String userID,ad,soyad;
    EditText tarih, tel, Kdetay;
    private DatePickerDialog kayipTarih;
    private SimpleDateFormat veriTarihFormat;
    private PaylasmaModel paylasmaModel;
    private ImageView kayipResim;
    private static final int RESIM_ISTEK= 123;
    private Uri dosyaYolu;
    private FirebaseAuth mKullanici;
    private FirebaseStorage firebaseDepolama;
    private DatabaseReference mVeritabani;
    private Button kayipPaylas;
    private String paylasmaKonu, il, ilce;
    private ProgressDialog ekleDiyalog;

    final Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.GRAY)
            .borderWidthDp(4)
            .cornerRadiusDp(35)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasim_ekle);
        tarih = (EditText) findViewById(R.id.tarih);
        tel = (EditText) findViewById(R.id.tel);
        spinnerKonu = (Spinner) findViewById(R.id.konu);
        kayipResim = (ImageView) findViewById(R.id.resim);
        kayipPaylas = (Button) findViewById(R.id.paylas);
        Kdetay = (EditText) findViewById(R.id.detay);
        getSupportActionBar().setTitle(R.string.paylasim_ekle);


        mKullanici = FirebaseAuth.getInstance();
        firebaseDepolama = FirebaseStorage.getInstance();


        kayipResim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KayipResimSec();
            }
        });


        //Paylaşım Konusu spinner doldurma
        final List<String> spinnerKonuData = new ArrayList<>();
        spinnerKonuData.add("Evcil Hayvanım Kayıp");
        spinnerKonuData.add("Bir Yakınım Kayıp");
        spinnerKonuData.add("Çocuğum Kayıp");


        final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(PaylasimEkle.this,
                android.R.layout.simple_spinner_item, spinnerKonuData);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKonu.setAdapter(dataAdapter2);
        paylasmaKonu = spinnerKonu.getSelectedItem().toString();

        //Tarih işlemi için fonksiyon kullanımı

        veriTarihFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);
        setDateTimeField();
        tarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kayipTarih.show();
            }
        });
        //Json dosyası işlemleri
        SehirIlceModel illist = new SehirIlceModel();
        try {
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.ililcejson)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }
            Gson gson = new Gson();
            illist = gson.fromJson(jsonBuilder.toString(), SehirIlceModel.class);

            Log.d("Deneme", illist.getIller().get(0).getIl());

        } catch (FileNotFoundException e) {
            Log.e("jsonFile", "file not found");
        } catch (IOException e) {
            Log.e("jsonFile", "ioerror");
        }


        final Spinner spinnerIller = (Spinner) findViewById(R.id.il);
        final Spinner spinnerIlceler = (Spinner) findViewById(R.id.ilce);

        final List<String> spinnerIlData = new ArrayList<>();

        for (int i = 0; i < illist.getIller().size(); i++) {
            spinnerIlData.add(illist.getIller().get(i).getIl());

        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        Log.d("userID:", userID);

        mVeritabani = FirebaseDatabase.getInstance().getReference();

        mVeritabani.child("kullanicilar").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KullaniciModel kullaniciModel = dataSnapshot.getValue(KullaniciModel.class);
                ad=kullaniciModel.getAd();
                soyad=kullaniciModel.getSoyad();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //İl spinner ı dolduruldu
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerIlData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIller.setAdapter(dataAdapter);


        final SehirIlceModel finalIllist1 = illist;

        spinnerIller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                il = spinnerIller.getSelectedItem().toString();
                //ilce spinnerı dolduruluyor.
                String[] ilceler = String.valueOf(finalIllist1.getIller().get(i).getIlceleri()).split(",");
                final List<String> spinnerIlceData = new ArrayList<>();
                final ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(PaylasimEkle.this,
                        android.R.layout.simple_spinner_item, spinnerIlceData);
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                for (int a = 0; a < ilceler.length; a++) {
                    Log.d("ilceler", ilceler[a]);
                    spinnerIlceData.add(ilceler[a]);
                }
                spinnerIlceler.setAdapter(dataAdapter1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerIlceler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ilce = spinnerIlceler.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        kayipPaylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(paylasmaModel==null){
                    //paylasmaModel=new PaylasmaModel(paylasmaKonu,il,ilce,tel.getText().toString(),tarih.getText().toString(),String.valueOf(filePath),Kdetay.getText().toString(),userID,ad,soyad);
                    paylasmaModel=new PaylasmaModel();
                    paylasmaModel.setPaylasmaKonusu(paylasmaKonu);
                    paylasmaModel.setIl(il);
                    paylasmaModel.setIlce(ilce);
                    paylasmaModel.setTel(tel.getText().toString());
                    paylasmaModel.setTarih(tarih.getText().toString());
                    paylasmaModel.setResimpath(String.valueOf(dosyaYolu));
                    paylasmaModel.setKayipDetay(Kdetay.getText().toString());
                    paylasmaModel.setId(userID);
                    paylasmaModel.setAd(ad);
                    paylasmaModel.setSoyad(soyad);
                    mVeritabani.child("PaylasilanKayip").child(userID).setValue(paylasmaModel);

                }


                if (dosyaYolu != null) {
                    ekleDiyalog = new ProgressDialog(PaylasimEkle.this);
                    ekleDiyalog.setMessage("Paylaşım Ekleniyor...");
                    ekleDiyalog.setCancelable(false);
                    ekleDiyalog.show();
                    StorageReference storageRef = firebaseDepolama.getReference().child("paylasResim").child(mKullanici.getCurrentUser().getUid());
                    storageRef.putFile(dosyaYolu).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ekleDiyalog.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.lin2), "Kayıp Paylaşımı Eklendi!", Snackbar.LENGTH_LONG);
                            snackbar.show();

                            //Toast.makeText(ProfilDuzenle.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();
                            kayipResim.setImageBitmap(null);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //progressDialog.dismiss();
                            Toast.makeText(PaylasimEkle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


                }

            }
        });


    }

    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        kayipTarih = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tarih.setText(veriTarihFormat.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void KayipResimSec() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), RESIM_ISTEK);
    }

    @Override
    protected void onActivityResult(int istekKod, int sonucKod, Intent veri) {
        super.onActivityResult(istekKod, sonucKod, veri);

        if (istekKod == RESIM_ISTEK && sonucKod == RESULT_OK && veri != null && veri.getData() != null) {
            dosyaYolu = veri.getData();
            try {
                Picasso.with(PaylasimEkle.this).load(dosyaYolu).resize(800, 300).into(kayipResim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}