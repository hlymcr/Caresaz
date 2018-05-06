package com.hsn.caresaz.caresaz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

public class GirisKayit extends AppCompatActivity {

    final Context context = this;

    AutoCompleteTextView eposta, kayitEposta;
    DatabaseReference veritabaniReferans;
    EditText sifre, kayitSifre;
    TextView kayit;
    Button giris, kayitOl, iptal;
    ImageView sifreU;
    ImageView resim;
    ImageView turkiye, ingilizce;
    FirebaseAuth mKullanici;
    private static final String TAG = "EpostaSifre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_kayit);

        //Firebase bağlantı
        mKullanici = FirebaseAuth.getInstance();

        eposta = (AutoCompleteTextView) findViewById(R.id.eposta);
        sifre = (EditText) findViewById(R.id.sifre);
        kayit = (TextView) findViewById(R.id.kayit);
        giris = (Button) findViewById(R.id.giris);

        turkiye = (ImageView) findViewById(R.id.turkey);
        ingilizce = (ImageView) findViewById(R.id.english);

        turkiye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale = new Locale("");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                finish();
                startActivity(getIntent());
            }
        });


        ingilizce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                finish();
                startActivity(getIntent());
            }
        });


        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kayitDialog();
            }
        });
        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(eposta.getText().toString(), sifre.getText().toString());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mKullanici.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(String eposta, String sifre) {
        Log.d(TAG, "signIn:" + eposta);
        if (!girisOnayForm()) {
            return;
        }
        mKullanici.signInWithEmailAndPassword(eposta, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mKullanici.getCurrentUser();
                            Toast.makeText(GirisKayit.this, "Giriş Başarılı.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GirisKayit.this, AnaSayfa.class);
                            updateUI(user);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(GirisKayit.this, "Kimlik Doğrulama Başarısız oldu.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(GirisKayit.this, "Kimlik Doğrulama Başarısız oldu", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    public void kayitDialog() {
        // dialog nesnesi oluştur ve layout dosyasına bağlan
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_kayit_dialog);

        kayitEposta = (AutoCompleteTextView) dialog.findViewById(R.id.kayitEposta);
        kayitSifre = (EditText) dialog.findViewById(R.id.kayitSifre);
        kayitOl = (Button) dialog.findViewById(R.id.kayitOl);
        iptal = (Button) dialog.findViewById(R.id.iptal);


        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(kayitEposta.getText().toString(), kayitSifre.getText().toString());
            }
        });


        iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void createAccount(String eposta, String sifre) {
        if (!kayitOnayForm()) {
            return;
        }
        mKullanici.createUserWithEmailAndPassword(eposta, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> görev) {
                        if (görev.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            veritabaniReferans = FirebaseDatabase.getInstance().getReference("kullanicilar/" + uid);
                            EmailDogrulama();
                            FirebaseUser user = mKullanici.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(GirisKayit.this, "Doğrulama Başarısız",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(this, AnaSayfa.class);
            startActivity(i);
            finish();
        } else {

        }

    }

    private boolean kayitOnayForm() {
        //Kayıt Dialog kontrol
        boolean valid = true;

        String email = kayitEposta.getText().toString();
        if (TextUtils.isEmpty(email)) { //Boşsa "Gerekli" uyarısını ver
            kayitEposta.setError("Gerekli.");
            valid = false;
        } else {
            kayitEposta.setError(null);
        }

        String password = kayitSifre.getText().toString();
        if (TextUtils.isEmpty(password)) { //Boşsa "Gerekli" uyarısını ver
            kayitSifre.setError("Gerekli.");
            valid = false;
        } else {
            kayitSifre.setError(null);
        }

        return valid;
    }

    private boolean girisOnayForm() {
        //Giriş eposta şifre kontrol
        boolean valid = true;

        String email = eposta.getText().toString();
        if (TextUtils.isEmpty(email)) {
            eposta.setError("Gerekli.");
            valid = false;
        } else {
            eposta.setError(null);
        }

        String password = sifre.getText().toString();
        if (TextUtils.isEmpty(password)) {
            sifre.setError("Gerekli.");
            valid = false;
        } else {
            sifre.setError(null);
        }

        return valid;
    }

    private void EmailDogrulama() {
        //Email Doğrulama gönderme
        final FirebaseUser user = mKullanici.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> görev) {
                        if (görev.isSuccessful()) {
                            Toast.makeText(GirisKayit.this,
                                    "\n" +
                                            "Adresine doğrulama e-postası gönderildi  " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", görev.getException());
                            Toast.makeText(GirisKayit.this,
                                    "\n" +
                                            "Doğrulama e-postası gönderilemedi.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
