package com.hsn.caresaz.caresaz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.storage.FirebaseStorage;
import com.hsn.caresaz.caresaz.adapter.CihazlarAdapter;
import com.hsn.caresaz.caresaz.model.Cihaz;
import java.util.ArrayList;
import java.util.List;


public class AnaSayfa extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static GoogleMap googleMap;
    private FloatingActionButton ekle;
    String userID;
    private DatabaseReference mDatabase;
    private FirebaseStorage fStorage;
    private TextView konum;
    private ListView list;
    private CihazlarAdapter cihazlarAdapter;
    private List<Cihaz> cihazList=null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ekle = (FloatingActionButton) findViewById(R.id.fab);
        final TextView cihaz=(TextView)findViewById(R.id.textView3);
        setSupportActionBar(toolbar);
        konum = (TextView) findViewById(R.id.konum);
        konum.setText("Konumunuz");

        firebaseAuth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnaSayfa.this, CihazEkle.class);
                startActivity(intent);

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = user.getUid();

        Log.d("userID:", userID);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("cihazlar");


        list = (ListView) findViewById(R.id.listView);

        cihazList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = String.valueOf(dataSnapshot.child(userID).getKey());
                Log.d("getkey", String.valueOf(dataSnapshot.child(userID).getKey()));

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("key12", postSnapshot.getKey());
                   /* final String isim = postSnapshot.child("isim").getValue().toString();
                    final String tur = postSnapshot.child("tur").getValue().toString();
                    final String resim = postSnapshot.child("resim").getValue().toString();
                    final String kod = postSnapshot.child("kod").getValue().toString();
                    cihazList.add(new Cihaz(isim,tur,resim,kod));*/
                    if(postSnapshot.getKey().equals(userID)){
                        Cihaz cihaz = postSnapshot.getValue(Cihaz.class);
                        cihazList.add(cihaz);
                    }
                    else{
                        cihaz.setText(R.string.cihaz_yok);
                    }

                }

                cihazlarAdapter = new CihazlarAdapter(getApplicationContext(), cihazList);
                list.setAdapter(cihazlarAdapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AnaSayfa.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fetchData process = new fetchData();
                process.execute();
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

    /*
       * Called when the Activity becomes visible.
      */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
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
        // Display the connection status

    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        double latidue = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latidue, longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        googleMap.setOnMyLocationButtonClickListener(null);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latidue, longitude)).title("Konumum");
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        googleMap.addMarker(marker);

    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ana_sayfa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profil) {

            Intent intent = new Intent(this, KullaniciProfil.class);
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AnaSayfa.this);
        builder1.setMessage("Çıkış yapmak ister misin?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Evet",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        dialog.cancel();
                        startActivity(new Intent(AnaSayfa.this, GirisKayit.class));
                        finish();

                    }
                });

        builder1.setNegativeButton(
                "Hayır",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}
