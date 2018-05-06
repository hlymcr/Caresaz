package com.hsn.caresaz.caresaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.hsn.caresaz.caresaz.ProfilResimtasarim.RoundedTransformationBuilder;
import com.hsn.caresaz.caresaz.model.KullaniciModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Map;

public class ProfilDuzenle extends AppCompatActivity {

    private String[] iller = {"Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin", "Aydın", "Balıkesir", "Bartın",
            "Batman", "Bayburt", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı",
            "Çorum", "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun",
            "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul", "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu",
            "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir", "Kilis", "Kocaeli", "Konya ", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla",
            "Muş", "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon", "Tunceli",
            "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak"
    };
    private Spinner sehirler;
    private ArrayAdapter<String> veriAdapterForIller;
    private EditText ad, soyad, tel;
    String adS, soyadS, telS, sehirS;
    private Button kaydet;
    String kullaniciID;
    private KullaniciModel kullaniciModel;
    private DatabaseReference mVeritabani;
    private Map<String, Object> postValues;
    private static final int RESIM_ISTEK = 123;
    private Uri filePath;
    private FirebaseAuth mKullanici;
    private FirebaseStorage firebaseDepolama;
    private ProgressDialog KayitDialog;
    ImageView resim;
    final Transformation ceviri = new RoundedTransformationBuilder()
            .borderColor(Color.GRAY)
            .borderWidthDp(4)
            .cornerRadiusDp(35)
            .oval(true)
            .build();

    @Override
    protected void onActivityResult(int istekKod, int sonucKod, Intent veri) {
        super.onActivityResult(istekKod, sonucKod, veri);
        if (istekKod == RESIM_ISTEK && sonucKod == RESULT_OK && veri != null && veri.getData() != null) {
            filePath = veri.getData();
            try {
                Picasso.with(ProfilDuzenle.this).load(filePath).fit().transform(ceviri).into(resim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_duzenle);

        ad = (EditText) findViewById(R.id.adi);
        soyad = (EditText) findViewById(R.id.soyad);
        tel = (EditText) findViewById(R.id.tel);
        resim = (ImageView) findViewById(R.id.resim);
        kaydet = (Button) findViewById(R.id.kaydet);
        sehirler = (Spinner) findViewById(R.id.sehir);
        kullaniciModel = new KullaniciModel();

        mKullanici = FirebaseAuth.getInstance();
        firebaseDepolama = FirebaseStorage.getInstance();
        KayitDialog = new ProgressDialog(ProfilDuzenle.this);
        KayitDialog.setMessage("Yükleniyor...");
        KayitDialog.setCancelable(false);
        KayitDialog.show();
        StorageReference depolamaReferans = firebaseDepolama.getReference().child("users").child(mKullanici.getCurrentUser().getUid());
        depolamaReferans.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                KayitDialog.dismiss();
                Picasso.with(ProfilDuzenle.this).load(uri).fit().transform(ceviri).into(resim);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                KayitDialog.dismiss();
                Toast.makeText(ProfilDuzenle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        veriAdapterForIller = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, iller);
        veriAdapterForIller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sehirler.setAdapter(veriAdapterForIller);

        sehirler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getBaseContext(), "" + sehirler.getSelectedItem().toString(), Toast.LENGTH_LONG);
                sehirS = sehirler.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        kullaniciID = user.getUid();
        mVeritabani = FirebaseDatabase.getInstance().getReference();
        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (kullaniciModel == null) {

                    kullaniciModel.setAd(ad.getText().toString());
                    kullaniciModel.setSoyad(soyad.getText().toString());
                    kullaniciModel.setTel(tel.getText().toString());
                    kullaniciModel.setSehir(sehirS);
                    mVeritabani.child("kullanicilar").child(kullaniciID).setValue(kullaniciModel);
                    Toast.makeText(ProfilDuzenle.this, "Kaydedildi", Toast.LENGTH_SHORT).show();
                    Log.i("SAVE", "saveEntry: Kaydedildi.");

                    if (filePath != null) {
                        KayitDialog = new ProgressDialog(ProfilDuzenle.this);
                        KayitDialog.setMessage("Yükleniyor...");
                        KayitDialog.setCancelable(false);
                        KayitDialog.show();
                        StorageReference depolamaReferans = firebaseDepolama.getReference().child("users").child(mKullanici.getCurrentUser().getUid());
                        depolamaReferans.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                KayitDialog.dismiss();
                                //Toast.makeText(ProfilDuzenle.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();
                                resim.setImageBitmap(null);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                KayitDialog.dismiss();
                                Toast.makeText(ProfilDuzenle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                } else {
                    kullaniciModel.setAd(ad.getText().toString());
                    kullaniciModel.setSoyad(soyad.getText().toString());
                    kullaniciModel.setTel(tel.getText().toString());
                    kullaniciModel.setSehir(sehirS);
                    postValues = kullaniciModel.toMap();
                    mVeritabani.child("kullanicilar").child(kullaniciID).updateChildren(postValues);
                    Toast.makeText(ProfilDuzenle.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                    Log.i("SAVE", "saveEntry: Güncellendi.");
                    if (filePath != null) {
                        KayitDialog = new ProgressDialog(ProfilDuzenle.this);
                        KayitDialog.setMessage("Yükleniyor...");
                        KayitDialog.setCancelable(false);
                        KayitDialog.show();
                        StorageReference storageRef = firebaseDepolama.getReference().child("users").child(mKullanici.getCurrentUser().getUid());
                        storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                KayitDialog.dismiss();
                                //Toast.makeText(ProfilDuzenle.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();
                                resim.setImageBitmap(null);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                KayitDialog.dismiss();
                                Toast.makeText(ProfilDuzenle.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }

            }


        });
        mVeritabani.child("kullanicilar").child(kullaniciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dsveri) {
                KullaniciModel kullaniciModel = dsveri.getValue(KullaniciModel.class);
                if (kullaniciModel == null) {
                    Toast.makeText(ProfilDuzenle.this, "Lütfen Profil bilgilerini ekleyiniz", Toast.LENGTH_SHORT).show();
                } else {
                    adS = kullaniciModel.getAd();
                    soyadS = kullaniciModel.getSoyad();
                    telS = kullaniciModel.getTel();
                    sehirS = kullaniciModel.getSehir();
                    ad.setText(adS);
                    soyad.setText(soyadS);
                    tel.setText(telS);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Hata", "Hata mesajı");
            }
        });
    }

    public void FotografDegistir(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), RESIM_ISTEK);
    }

}
