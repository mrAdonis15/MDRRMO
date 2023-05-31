package com.adonis_development_design12.mdrrmo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registering_Addresses extends AppCompatActivity {

    private Spinner spinnerProvince;
    private Spinner spinnerMunicipality;
    private EditText editTextHospitalName;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private Button addButton;

    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerMunicipality = findViewById(R.id.spinner_municipality);
        editTextHospitalName = findViewById(R.id.editTextHospitalName);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        editTextLatitude = findViewById(R.id.editTextLatitude);

        addButton = findViewById(R.id.addButton);

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String province = spinnerProvince.getSelectedItem().toString();
                int municipalityArrayId = getResources().getIdentifier("municipality_" + province.toLowerCase().replace(" ", "_"), "array", getPackageName());
                ArrayAdapter<CharSequence> municipalityAdapter = ArrayAdapter.createFromResource(Registering_Addresses.this, municipalityArrayId, android.R.layout.simple_spinner_item);
                municipalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMunicipality.setAdapter(municipalityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String province = spinnerProvince.getSelectedItem().toString();
                String municipality = spinnerMunicipality.getSelectedItem().toString();
                String hospitalName = editTextHospitalName.getText().toString().trim();
                String longitude = editTextLongitude.getText().toString().trim();
                String latitude = editTextLatitude.getText().toString().trim();

                if (hospitalName.isEmpty() || longitude.isEmpty() || latitude.isEmpty()) {
                    showToast("Please fill all the fields");
                } else {
                    saveDataToFirebase(province, municipality, hospitalName, longitude, latitude);
                }
            }
        });

    }

    private void saveDataToFirebase(String province, String municipality, String hospitalName, String longitude, String latitude) {
        DatabaseReference provinceRef = databaseReference.child("province").child(province);
        DatabaseReference municipalityRef = provinceRef.child("municipality").child(municipality);
        DatabaseReference hospitalRef = municipalityRef.child("hospital").child(hospitalName);

        hospitalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showToast("Data already exists");
                } else {
                    hospitalRef.child("longitude").setValue(longitude);
                    hospitalRef.child("latitude").setValue(latitude);
                    showToast("Data saved successfully");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("An error occurred");
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
