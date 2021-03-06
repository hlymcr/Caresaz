package com.hsn.caresaz.caresaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsn.caresaz.caresaz.adapter.PaylasilanAdapter;
import com.hsn.caresaz.caresaz.model.PaylasmaModel;

import java.util.ArrayList;
import java.util.List;

public class Paylasimlar extends AppCompatActivity {
    private FirebaseAuth firebaseKullanicilar;
    private DatabaseReference veritabaniReferans;
    private ListView list;
    private PaylasilanAdapter paylasilanAdapter;
    private List<PaylasmaModel> paylasilanList;
    private ProgressDialog yüklemeDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasimlar);
        getSupportActionBar().setTitle(R.string.paylasimlar);



        FloatingActionButton paylas = (FloatingActionButton) findViewById(R.id.fab);
        paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Paylasimlar.this,PaylasimEkle.class);
                startActivity(intent);
            }
        });


        firebaseKullanicilar = FirebaseAuth.getInstance();
        veritabaniReferans = FirebaseDatabase.getInstance().getReference("PaylasilanKayip");

        list = (ListView) findViewById(R.id.listView);

        paylasilanList = new ArrayList<>();

        yüklemeDialog = new ProgressDialog(Paylasimlar.this);
        yüklemeDialog.setMessage("Yükleniyor...");
        yüklemeDialog.setCancelable(false);
        yüklemeDialog.show();

        veritabaniReferans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yüklemeDialog.dismiss();
                paylasilanList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PaylasmaModel userObj = postSnapshot.getValue(PaylasmaModel.class);
                    paylasilanList.add(userObj);
                }

                paylasilanAdapter = new PaylasilanAdapter(getApplicationContext(),paylasilanList);
                list.setAdapter(paylasilanAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                yüklemeDialog.dismiss();
                Toast.makeText(Paylasimlar.this, "DATABASE error", Toast.LENGTH_SHORT).show();
            }
        });




    }
    @Override
    protected void onResume() {
        super.onResume();
        if(paylasilanAdapter!=null){
            paylasilanAdapter.notifyDataSetChanged();
        }
    }

}
