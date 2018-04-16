package com.hsn.caresaz.caresaz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.hsn.caresaz.caresaz.KisiModel;
import com.hsn.caresaz.caresaz.ProfilDuzenle;
import com.hsn.caresaz.caresaz.R;
import com.hsn.caresaz.caresaz.RoundedTransformationBuilder;
import com.hsn.caresaz.caresaz.model.PaylasmaModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HULYA on 8.04.2018.
 */

public class PaylasilanAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    ArrayList<PaylasmaModel> paylasilanlist;

    FirebaseDatabase database;
    private String userID;
    private DatabaseReference mDatabase;
    private ImageView kullaniciResim,kayipResim;
    private TextView paylasKonu;
    private FirebaseStorage fStorage;
    private FirebaseAuth mAuth;
    private TextView isim;

    public PaylasilanAdapter(Context activity, List<PaylasmaModel> paylasilanlist) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paylasilanlist = (ArrayList<PaylasmaModel>) paylasilanlist;


    }
    @Override
    public int getCount() {
        return paylasilanlist.size();
    }

    @Override
    public Object getItem(int i) {
        return paylasilanlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PaylasmaModel paylasmaModel =paylasilanlist.get(i);
        final View satir = layoutInflater.inflate(R.layout.paylasilan_list_item, null);
        database = FirebaseDatabase.getInstance();
        isim=satir.findViewById(R.id.isim);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        Log.d("userID:", userID);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();


        //Profil resimlerini oval yapmayı sağlayan metod
        final Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(4)
                .cornerRadiusDp(35)
                .oval(true)
                .build();

        kullaniciResim = (ImageView)satir.findViewById(R.id.kullaniciresim);
        kayipResim = (ImageView)satir.findViewById(R.id.kayipresim);
        paylasKonu = (TextView)satir.findViewById(R.id.konubaslik);

        paylasKonu.setText(paylasmaModel.getPaylasmaKonusu());

        StorageReference storageRef = fStorage.getReference().child("users").child(paylasmaModel.getId());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(satir.getContext()).load(uri).fit().transform(transformation).into(kullaniciResim);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(satir.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        StorageReference storageReference = fStorage.getReference().child("paylasResim").child(paylasmaModel.getId());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(satir.getContext()).load(uri).fit().into(kayipResim);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(satir.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return satir;
    }
}
