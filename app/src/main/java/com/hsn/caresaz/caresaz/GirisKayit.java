package com.hsn.caresaz.caresaz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

public class GirisKayit extends AppCompatActivity {

    final Context context = this;

    AutoCompleteTextView eposta, kayitEposta;
    DatabaseReference databaseReference;
    EditText sifre, kayitSifre;
    TextView kayit;
    Button giris, kayitOl, iptal;
    ImageView sifreU;
    ImageView resim;

    FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_kayit);

        //Firebase bağlantı
        mAuth = FirebaseAuth.getInstance();

        eposta = (AutoCompleteTextView) findViewById(R.id.eposta);
        sifre = (EditText) findViewById(R.id.sifre);
        kayit = (TextView) findViewById(R.id.kayit);
        giris = (Button) findViewById(R.id.giris);



        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyCustomAlertDialog();
            }
        });
        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(eposta.getText().toString(),sifre.getText().toString());
            }
        });

    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void signIn(String eposta, String sifre) {
        Log.d(TAG, "signIn:" + eposta);
        if (!girisValidateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(eposta, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(GirisKayit.this, "Giriş Başarılı.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GirisKayit.this, AnaSayfa.class);
                            updateUI(user);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(GirisKayit.this, "Kimlik Doğrulama Başarısız oldu.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(GirisKayit.this,"Kimlik Doğrulama Başarısız oldu",Toast.LENGTH_LONG);
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public void showMyCustomAlertDialog() {
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
        Log.d(TAG, "createAccount:" + eposta);
        if (!kayitValidateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(eposta, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                            databaseReference= FirebaseDatabase.getInstance().getReference("kullanicilar/"+uid);
                            sendEmailVerification();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(GirisKayit.this, "Doğrulama Başarısız",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
        // [END create_user_with_email]


    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(this,AnaSayfa.class);
            startActivity(i);
            finish();
        } else {

        }

    }

    private boolean kayitValidateForm() {
        //Kayıt Dialog kontrol
        boolean valid = true;

        String email = kayitEposta.getText().toString();
        if (TextUtils.isEmpty(email)){ //Boşsa "Gerekli" uyarısını ver
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
    private boolean girisValidateForm() {
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

    private void sendEmailVerification() {
        //Email Doğrulama gönderme
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(GirisKayit.this,
                                    "\n" +
                                            "Adresine doğrulama e-postası gönderildi  " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(GirisKayit.this,
                                    "\n" +
                                            "Doğrulama e-postası gönderilemedi.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]

    }

}
