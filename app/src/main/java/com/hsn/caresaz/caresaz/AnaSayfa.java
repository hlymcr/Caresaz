package com.hsn.caresaz.caresaz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsn.caresaz.caresaz.adapter.CihazlarAdapter;
import com.hsn.caresaz.caresaz.model.Cihaz;

import java.util.ArrayList;
import java.util.List;


public class AnaSayfa extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private FirebaseAuth firebaseKullanici;
    private DatabaseReference veritabaniReferans;
    public static GoogleMap googleHarita;
    private FloatingActionButton cihazekle;
    private String kullaniciID;
    private TextView konum;
    private ListView liste;
    private CihazlarAdapter cihazlarAdapter;
    private List<Cihaz> cihazListe = null;
    private TextView nabiz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cihazekle = (FloatingActionButton) findViewById(R.id.fab);
        final TextView cihaz = (TextView) findViewById(R.id.textView3);
        liste = (ListView) findViewById(R.id.listView);

        //nabiz = (TextView) findViewById(R.id.nabizText);

        setSupportActionBar(toolbar);
        konum = (TextView) findViewById(R.id.konum);
        konum.setText(R.string.konum);

        firebaseKullanici = FirebaseAuth.getInstance();

        cihazekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnaSayfa.this, CihazEkle.class);
                startActivity(intent);

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseKullanici = FirebaseAuth.getInstance();
        kullaniciID = user.getUid();

        veritabaniReferans = FirebaseDatabase.getInstance().getReference("cihazlar");

        cihazListe = new ArrayList<>();

        veritabaniReferans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = String.valueOf(dataSnapshot.child(kullaniciID).getKey());
                Log.d("getkey", String.valueOf(dataSnapshot.child(kullaniciID).getKey()));

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("key12", postSnapshot.getKey());
                    if (postSnapshot.getKey().equals(kullaniciID)) {
                        Cihaz cihaz = postSnapshot.getValue(Cihaz.class);
                        cihazListe.add(cihaz);
                    } else {
                        cihaz.setText(R.string.cihaz_yok);
                    }
                }
                cihazlarAdapter = new CihazlarAdapter(getApplicationContext(), cihazListe);
                liste.setAdapter(cihazlarAdapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AnaSayfa.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vericekme process = new vericekme();
                process.execute();
                konum.setText(R.string.cihazKonum);
            }
        });


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (cihazlarAdapter != null) {
            cihazlarAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleHarita = map;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleHarita.setMyLocationEnabled(true);
    }

    @Override
    public void onMyLocationChange(Location location) {
        double latidue = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latidue, longitude);
        googleHarita.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        googleHarita.setOnMyLocationButtonClickListener(null);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latidue, longitude)).title("Cihazın Konumu");
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        googleHarita.addMarker(marker);
    }

    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ana_sayfa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profil) {

            Intent intent = new Intent(this, ProfilDuzenle.class);
            startActivity(intent);

        } else if (id == R.id.nav_aktivasyon) {
            Intent intent = new Intent(this, CihazEkle.class);
            startActivity(intent);
        } else if (id == R.id.nav_bildirim) {

        } else if (id == R.id.nav_paylasimlar) {

            Intent intent = new Intent(this, Paylasimlar.class);
            startActivity(intent);


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_cikis) {
            cikisyap();

        } else if (id == R.id.nav_hakkimizda) {

        } else if (id == R.id.nav_yardim) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cikisyap() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AnaSayfa.this);
        dialog.setMessage(R.string.cikissoru);
        dialog.setCancelable(true);

        dialog.setPositiveButton(
                R.string.evet,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseKullanici.signOut();
                        dialog.cancel();
                        startActivity(new Intent(AnaSayfa.this, GirisKayit.class));
                        finish();

                    }
                });

        dialog.setNegativeButton(
                R.string.hayir,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog1 = dialog.create();
        dialog1.show();
    }
}
