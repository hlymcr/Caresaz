package com.hsn.caresaz.caresaz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

public class PaylasilanAdapter extends ArrayAdapter<PaylasmaModel> {

    LayoutInflater layoutInflater;
    ArrayList<PaylasmaModel> paylasilanlist;

    FirebaseDatabase database;
    private String userID;
    private DatabaseReference mDatabase;
    private ImageView kullaniciResim, kayipResim;
    private TextView paylasKonu;
    private FirebaseStorage fStorage;
    private FirebaseAuth mAuth;
    private TextView isim;
    private Context context;
    //Profil resimlerini oval yapmayı sağlayan metod
    final Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.GRAY)
            .borderWidthDp(4)
            .cornerRadiusDp(35)
            .oval(true)
            .build();

    public PaylasilanAdapter(Context context, List<PaylasmaModel> paylasilanlist) {
        super(context, 0, paylasilanlist);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paylasilanlist = (ArrayList<PaylasmaModel>) paylasilanlist;
        this.context = context;


    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if(v==null){
            LayoutInflater vi =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.paylasilan_list_item, null);
            holder = new ViewHolder();
            holder.userName=(TextView)v.findViewById(R.id.isim);
            holder.konubaslik=(TextView)v.findViewById(R.id.konubaslik);
            holder.userImage=(ImageView)v.findViewById(R.id.kullaniciresim);
            holder.kayipResim=(ImageView)v.findViewById(R.id.kayipresim);
            v.setTag(holder);

        }
        else {
            holder = (ViewHolder) v.getTag();
        }
        PaylasmaModel paylasmaModel = paylasilanlist.get(position);
        holder.userName.setText(paylasmaModel.getAd()+" "+paylasmaModel.getSoyad());
        holder.konubaslik.setText(paylasmaModel.getPaylasmaKonusu());
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = fStorage.getReference().child("users").child(paylasmaModel.getId());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(context).load(uri).fit().transform(transformation).centerCrop().into(holder.userImage);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });
        StorageReference storageRef2 = fStorage.getReference().child("paylasResim").child(paylasmaModel.getId());
        storageRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(context).load(uri).fit().centerCrop().into(holder.kayipResim);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });
        return v;


    }
    static class ViewHolder {
        TextView userName;
        TextView konubaslik;
        ImageView userImage;
        ImageView kayipResim;
    }
}

