package com.app.pujaconnectsc.View.UI.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class AboutUs extends AppCompatActivity {
    private TextView txt;
    private MapView mapView;
    private IMapController mapController;
    private ImageView fb, insta, gmail, web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setTitle("About Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txt = findViewById(R.id.txtAboutUsTxt);
        txt.setText(getString(R.string.about_us));
        fb = findViewById(R.id.icFB);
        insta = findViewById(R.id.icInsta);
        gmail = findViewById(R.id.icGmail);
        web = findViewById(R.id.icWeb);
        osminit();
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutUs.this, "Open facebook", Toast.LENGTH_SHORT).show();
                openUrl("http://www.facebook.com");
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutUs.this, "Open insta", Toast.LENGTH_SHORT).show();
                openUrl("http://www.instagram.com");
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutUs.this, "Open gmail", Toast.LENGTH_SHORT).show();
                openUrl("http://www.gmail.com");
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutUs.this, "Open page", Toast.LENGTH_SHORT).show();
                openUrl("http://www.pujamandu.com");
            }
        });
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void osminit() {
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        mapView = (MapView) findViewById(R.id.mapviewAbout);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(27.71285750923292, 85.34759762258595);
        mapController.setCenter(startPoint);
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

    }
}