package com.adonis_development_design12.mdrrmo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Front_Face extends AppCompatActivity {

    private FirebaseDatabase database;
    private Spinner spinnerProvince;
    private Spinner spinnerMunicipality;
    private Spinner spinnerHospital;
    private Spinner spinnerLongitude;
    private Spinner spinnerLatitude;
    private Map<String, Map<String, String>> hospitalsMap;
    private Button goButton;
    private ProgressDialog progressDialog;

    private TextView logoutEditText;

    // Inside your activity or fragment class
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);


        // Hide the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Show the system navigation panel
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();

        // Initialize spinners
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerMunicipality = findViewById(R.id.spinner_municipality);
        spinnerHospital = findViewById(R.id.spinner_hospital);
        spinnerLongitude = findViewById(R.id.spinner_longitude);
        spinnerLatitude = findViewById(R.id.spinner_latitude);
        goButton = findViewById(R.id.button);

        // Find the EditText for logout by its ID
        logoutEditText = findViewById(R.id.Logout_Account);

        firebaseAuth = FirebaseAuth.getInstance();

        logoutEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                firebaseAuth.signOut();

                // Navigate back to the Create_Login activity
                Intent intent = new Intent(Front_Face.this, Create_Login.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent navigating back to it

                // Show a toast message indicating successful logout
                Toast.makeText(Front_Face.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude_str = spinnerLatitude.getSelectedItem().toString();
                String longitude_str = spinnerLongitude.getSelectedItem().toString();

                Intent intent = new Intent(Front_Face.this, MapsActivity.class);
                intent.putExtra("latitude", latitude_str);
                intent.putExtra("longitude", longitude_str);
                startActivity(intent);
            }
        });

        // Retrieve provinces
        retrieveProvinces();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Coordinates...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void retrieveProvinces() {
        database.getReference("province").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> provinces = new ArrayList<>();
                for (DataSnapshot provinceSnapshot : dataSnapshot.getChildren()) {
                    String provinceName = provinceSnapshot.getKey();
                    provinces.add(provinceName);
                }
                updateProvincesSpinner(provinces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database retrieval
            }
        });
    }

    private void retrieveMunicipalities(String selectedProvince) {
        database.getReference("province").child(selectedProvince).child("municipality")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> municipalities = new ArrayList<>();
                        for (DataSnapshot municipalitySnapshot : dataSnapshot.getChildren()) {
                            String municipalityName = municipalitySnapshot.getKey();
                            municipalities.add(municipalityName);
                        }
                        updateMunicipalitiesSpinner(municipalities);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur during the database retrieval
                    }
                });
    }

    private void retrieveHospitals(String selectedProvince, String selectedMunicipality) {
        database.getReference("province").child(selectedProvince).child("municipality")
                .child(selectedMunicipality).child("hospital")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> hospitals = new ArrayList<>();
                        hospitalsMap = new HashMap<>();
                        for (DataSnapshot hospitalSnapshot : dataSnapshot.getChildren()) {
                            String hospitalName = hospitalSnapshot.getKey();
                            hospitals.add(hospitalName);

                            // Retrieve longitude and latitude for each hospital
                            String latitude = hospitalSnapshot.child("latitude").getValue(String.class);
                            String longitude = hospitalSnapshot.child("longitude").getValue(String.class);
                            Map<String, String> hospitalData = new HashMap<>();
                            hospitalData.put("latitude", latitude);
                            hospitalData.put("longitude", longitude);
                            hospitalsMap.put(hospitalName, hospitalData);
                        }
                        updateHospitalsSpinner(hospitals);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur during the database retrieval
                    }
                });
    }

    private void updateProvincesSpinner(List<String> provinces) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinces);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(adapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedProvince = adapterView.getItemAtPosition(position).toString();
                retrieveMunicipalities(selectedProvince);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where no province is selected
            }
        });
    }

    private void updateMunicipalitiesSpinner(List<String> municipalities) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, municipalities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipality.setAdapter(adapter);

        spinnerMunicipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedProvince = spinnerProvince.getSelectedItem().toString();
                String selectedMunicipality = adapterView.getItemAtPosition(position).toString();
                retrieveHospitals(selectedProvince, selectedMunicipality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where no municipality is selected
            }
        });
    }

    private void updateHospitalsSpinner(List<String> hospitals) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospitals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(adapter);

        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedHospital = adapterView.getItemAtPosition(position).toString();
                updateLongitudeSpinner(selectedHospital);
                updateLatitudeSpinner(selectedHospital);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where no hospital is selected
            }
        });
    }

    private void updateLongitudeSpinner(String selectedHospital) {
        if (hospitalsMap != null && hospitalsMap.containsKey(selectedHospital)) {
            String longitude = hospitalsMap.get(selectedHospital).get("longitude");
            List<String> longitudeList = new ArrayList<>();
            longitudeList.add(longitude);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, longitudeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLongitude.setAdapter(adapter);
        }
    }

    private void updateLatitudeSpinner(String selectedHospital) {
        if (hospitalsMap != null && hospitalsMap.containsKey(selectedHospital)) {
            String latitude = hospitalsMap.get(selectedHospital).get("latitude");
            List<String> latitudeList = new ArrayList<>();
            latitudeList.add(latitude);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, latitudeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLatitude.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Change activity when tapping on textView5
        TextView textView5 = findViewById(R.id.Add_Address);
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Front_Face.this, Registering_Addresses.class);
                startActivity(intent);
            }
        });
    }
}
