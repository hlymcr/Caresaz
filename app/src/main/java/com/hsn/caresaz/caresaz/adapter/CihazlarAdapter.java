package com.hsn.caresaz.caresaz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.hsn.caresaz.caresaz.R;
import com.hsn.caresaz.caresaz.RoundedTransformationBuilder;
import com.hsn.caresaz.caresaz.model.Cihaz;
import com.hsn.caresaz.caresaz.model.PaylasmaModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HULYA on 20.04.2018.
 */

public class CihazlarAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    ArrayList<Cihaz> cihazlist;

    FirebaseDatabase database;
    private String userID;
    private DatabaseReference mDatabase;
    private ImageView cihazResim;
    private TextView cihazisim;
    private FirebaseStorage fStorage;
    private FirebaseAuth mAuth;
    public CihazlarAdapter(Context activity, List<Cihaz> cihazlist) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cihazlist = (ArrayList<Cihaz>) cihazlist;


    }
    @Override
    public int getCount() {
        return cihazlist.size();
    }

    @Override
    public Object getItem(int i) {
        return cihazlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Cihaz cihazmodel = cihazlist.get(i);
        final View satir = layoutInflater.inflate(R.layout.cihaz_list_istem, null);
        cihazisim = (TextView)satir.findViewById(R.id.cisim);
        cihazResim =(ImageView)satir.findViewById(R.id.cresim);
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Log.d("userID:", userID);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();

        if(cihazmodel.getTur().equals("Tasma")){
            cihazisim.setText(cihazmodel.getIsim()+" "+cihazmodel.getTur()+"'sı");
        }


        //Profil resimlerini oval yapmayı sağlayan metod
        final Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(1)
                .cornerRadiusDp(35)
                .oval(true)
                .build();


        StorageReference storageReference = fStorage.getReference().child("cihazresim").child(userID).child(cihazmodel.getKod());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(satir.getContext()).load(uri).fit().transform(transformation).into(cihazResim);
                Log.d( "uri1", String.valueOf(uri));

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
