package com.app.pujaconnectsc.View.UI.User;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pujaconnectsc.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class OrderMap extends AppCompatDialogFragment {
    private String lat, lng;
    private String userLat, userLng;
    private MapDialogListener listener;
    private MapView mapView;
    private IMapController mapController;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.order_map_dialog, null);
        //getLatLng();
        Bundle args = getArguments();
        userLat = args.getString("UserLat");
        userLng = args.getString("UserLng");
        osminit(view);
        builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (lat == null && lng == null) {
                    listener.getLatLng(userLat, userLng);
                } else {
                    listener.getLatLng(lat, lng);
                }

            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MapDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Implement MapDialogListener");
        }
    }

    public interface MapDialogListener {
        void getLatLng(String lat, String lng);
    }
    private void osminit(View view) {
        double latitude = Double.parseDouble(userLat);
        double longtitude = Double.parseDouble(userLng);
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        mapView = (MapView) view.findViewById(R.id.osmMapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(latitude, longtitude);
        mapController.setCenter(startPoint);
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        MapEventsOverlay overlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
               // Toast.makeText(getContext(), "Lat : " + p.getLatitude() + "Lang : " + p.getLongitude(), Toast.LENGTH_SHORT).show();
                lat = String.valueOf(p.getLatitude());
                lng = String.valueOf(p.getLongitude());
                marker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                mapController.animateTo(p);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });
        mapView.getOverlays().add(overlay);
        mapView.getOverlays().add(marker);
    }
}


















