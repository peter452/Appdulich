package com.example.myapplication.src.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.OnClickItemSeach;
import com.example.myapplication.models.menu_reponse.Menu;
import com.example.myapplication.models.menu_reponse.MenuReponse;
import com.example.myapplication.models.place_reponse.Place;
import com.example.myapplication.models.place_reponse.PlaceReponse;
import com.example.myapplication.services.APIServices;
import com.example.myapplication.services.DataService;
import com.example.myapplication.src.Adapter.AdapterLisplace;
import com.example.myapplication.src.Adapter.AdapterMap;
import com.example.myapplication.src.Adapter.AdapterMenuMap;
import com.example.myapplication.src.dialog.ShowButtonSheetDoalogMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnClickItemSeach {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static  final  String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static int LOCATION_PERMISION_REQUEST_CODE=1234;
    private static final float DEFAUL_ZOOM = 12f;
    //widgets
    private EditText mSeachText;

    //var
    private Location mLocationStart;
    private boolean mLocationPermisonGranted = false;
    private GoogleMap mMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private ArrayList<Place>arrayListPlace = new ArrayList<>();
    private ArrayList<Place>arrayListSeach = new ArrayList<>();
    private AdapterLisplace adapterSearch;

    //FuseLocationProviderClient là để tương tác với vị trí bằng cách sử dụng nhà cung cấp location provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private RecyclerView recyclerviewMapSearch,RecyclerViewMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        anhxa();
        getDataMenuAll();
        listenerSeach();
        checkLocationPermison();
    }

    private void listenerSeach() {
        mSeachText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mSeachText.getText().toString().equals("")){
                    recyclerviewMapSearch.setVisibility(View.GONE);
                }else{
                    recyclerviewMapSearch.setVisibility(View.VISIBLE);
                    getDataSearch(mSeachText.getText().toString());
                }
            }
        });
    }

    private void getDataSearch(String text){
        DataService dataService = APIServices.getService();
        Call<PlaceReponse>call = dataService.getDataPlaceStrSearch(text);
        call.enqueue(new Callback<PlaceReponse>() {
            @Override
            public void onResponse(Call<PlaceReponse> call, Response<PlaceReponse> response) {
                Log.d("AAA","search: "+response.toString());
                if(response.isSuccessful()){
                    arrayListSeach = (ArrayList<Place>) response.body().getData();
                    adapterSearch = new AdapterLisplace(arrayListSeach, getApplicationContext(),R.layout.item_listplace,MapActivity.this);
                    recyclerviewMapSearch.setAdapter(adapterSearch);
                    adapterSearch.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PlaceReponse> call, Throwable t) {
                Log.d("AAA","errSearch: "+t.toString());
            }
        });
    }

    private void getDataMenuAll() {
        DataService dataService = APIServices.getService();
        Call<MenuReponse>call = dataService.getDataMenuAll();
        call.enqueue(new Callback<MenuReponse>() {
            @Override
            public void onResponse(Call<MenuReponse> call, Response<MenuReponse> response) {
                Log.d(TAG, "onResponse: get data menu all"+response.toString());
                if(response.isSuccessful()){
                    ArrayList<Menu>arrayList = (ArrayList<Menu>) response.body().getData();
                    AdapterMenuMap adapterMenuMap = new AdapterMenuMap(MapActivity.this,R.layout.layout_item_menu_map,arrayList);
                    RecyclerViewMenu.setAdapter(adapterMenuMap);
                }
            }

            @Override
            public void onFailure(Call<MenuReponse> call, Throwable t) {
                Log.d(TAG, "onFailure: get data menu all: "+t.toString());
            }
        });
    }

    public void addListMarkerByMenuMap(ArrayList<Place>arrayList){
        markers.clear();
        arrayListPlace.clear();
        mMap.clear();

        LatLng sydney = new LatLng(16.054489, 108.202154);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Da Nang City"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

        arrayListPlace = arrayList;
        for(Place place : arrayList){
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(place.getLat()), Double.valueOf(place.getLng())))
                    .title(place.getName()));
            markers.add(m);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Map is ready");
        mMap = googleMap;
        listenerOnclickMap();
    }

    private void anhxa() {
        mSeachText = findViewById(R.id.txt_search);
        RecyclerViewMenu = findViewById(R.id.RecyclerViewMenu);
        RecyclerViewMenu.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MapActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerViewMenu.setLayoutManager(linearLayoutManager);

        recyclerviewMapSearch = findViewById(R.id.recyclerviewMapSearch);
        recyclerviewMapSearch.setHasFixedSize(true);
        recyclerviewMapSearch.setLayoutManager(new GridLayoutManager(MapActivity.this,1));
    }


    private void initMap(){
        Log.d(TAG,"init Map: ");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //kiem tra su cho phep google map
    private void checkLocationPermison(){
        Log.d(TAG,"get Location Permison: getting location permison");
        String [] permisons = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermisonGranted = true;
                initMap();
                getDeviceLocation();
            }else {
               // Tuy nhiên, nếu quyền đó vẫn chưa được cấp, bạn có thể yêu cầu nó với requestPermissions
                ActivityCompat.requestPermissions(this,permisons,LOCATION_PERMISION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permisons,LOCATION_PERMISION_REQUEST_CODE);
        }
    }

    private void listenerOnclickMap() {
        if(mMap != null){
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (Place place : arrayListPlace){
                        if(place.getName().equals(marker.getTitle())){
                            ShowButtonSheetDoalogMap showButtonSheetDoalogMap = new ShowButtonSheetDoalogMap(place);
                            showButtonSheetDoalogMap.show(getSupportFragmentManager(),"AAA");
                        }
                    }
                    return false;
                }
            });
        }
    }


    //lay vi tri hien tai cua thiet bi
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermisonGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                //Để xử lý thành công và thất bại trong cùng một người nghe, hãy đính kèm OnCompleteListener
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            if(task.getResult()!=null){
                                Location currentLocation = (Location) task.getResult();
                                mLocationStart = currentLocation;
                                // function di chuyen camera den vi tri hien tai
                                moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAUL_ZOOM,
                                        "My location",true);
                                //danh dau vi tri tren ban do(cham mau xanh)
                                mMap.setMyLocationEnabled(true);
                                //khi su dung setMyLocationEnabled se co vi tri mac dinh tren man hinh
                                // muon thay doi vi tri do thi tat no di
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                            }else{
                                Toast.makeText(MapActivity.this, "No find location", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Log.d(TAG, "onComplete: currenlàm t location is null");
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException: "+e.getMessage());
        }
    }

    //di chuyen camera khi lay dc vi tri location
    public void moveCamera(LatLng latLng,float zoom, String title,boolean check){
        Log.d(TAG, "moveCamera: moving the camera to: last: "+latLng.latitude+", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestpermisionsResult: called");
        mLocationPermisonGranted = false;
        if(requestCode == LOCATION_PERMISION_REQUEST_CODE){
            if(grantResults.length > 0 ){
                for (int i =0 ; i < grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        mLocationPermisonGranted = false;
                        Log.d(TAG,"onRequestpermisionsResult: failed");
                        return;
                    }
                }
                Log.d(TAG,"onRequestpermisionsResult: granted");
                mLocationPermisonGranted = true;
                initMap();
            }
        }

    }

    @Override
    public void onOpenItemClick(ArrayList<Place> list, int position) {
        ArrayList<Place>arrayList = new ArrayList<>();
        arrayList.add(list.get(position));
        addListMarkerByMenuMap(arrayList);
        mSeachText.setText(null);
        recyclerviewMapSearch.setVisibility(View.GONE);
    }
}
