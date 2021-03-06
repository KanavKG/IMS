package team58.cs2340.donationtracker.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import team58.cs2340.donationtracker.models.Category;
import team58.cs2340.donationtracker.models.LocationsLocal;
import team58.cs2340.donationtracker.models.Location;
import team58.cs2340.donationtracker.models.CurrUserLocal;
import team58.cs2340.donationtracker.models.Donation;
import team58.cs2340.donationtracker.R;

/**
 * Class for add donation activity
 */
public class AddDonationActivity extends AppCompatActivity {

    private TextView nameTxt;
    private Spinner locationSpinner;
    private TextView value;
    private TextView shortDescription;
    private TextView fullDescription;
    private Spinner categorySpinner;
    private TextView comment;

    private String mPhotoPath;

    private FirebaseFirestore db;
    private StorageReference mStorage;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donation);
        LocationsLocal locationManager = LocationsLocal.getInstance();
        CurrUserLocal userManager = CurrUserLocal.getInstance();
        this.nameTxt = findViewById(R.id.name);
        this.locationSpinner = findViewById(R.id.locationSpinner);
        this.shortDescription = findViewById(R.id.shortDescription);
        this.fullDescription = findViewById(R.id.fullDescription);
        this.value = findViewById(R.id.value);
        this.categorySpinner = findViewById(R.id.categorySpinner);
        this.comment = findViewById(R.id.comment);
        Button takePhoto = findViewById(R.id.takePhotoBtn);
        this.photoView = findViewById(R.id.photo);
        this.db = FirebaseFirestore.getInstance();
        this.mStorage = FirebaseStorage.getInstance().getReference();

        //Disable button if user has no camera
        if (!hasCamera()) {
            takePhoto.setEnabled(false);
            takePhoto.setVisibility(View.GONE);
        }

        ArrayAdapter<Location> locationAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locationManager.getLocations());
        locationSpinner.setAdapter(locationAdapter);

        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(
                this,android.R.layout.simple_spinner_item, Category.values());
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryArrayAdapter);

        Intent intent = getIntent();
        Location location = (Location) intent.getSerializableExtra("location");

        if (userManager.getCurrentUserLocation() != null) {
            int spinnerPosition = locationAdapter.getPosition(location);
            locationSpinner.setSelection(spinnerPosition);
            locationSpinner.setEnabled(false);
        }
    }

    /**
     * Function executed when add donation button is clicked
     * @param view The button view
     */
    public void onAddClicked(View view) {
        final String name = this.nameTxt.getText().toString();
        final Location location = (Location) locationSpinner.getSelectedItem();
        Double value = Donation.getValue(this.value.getText().toString());
        String shortDescription = this.shortDescription.getText().toString();
        String fullDescription = this.fullDescription.getText().toString();
        Category category = (Category) categorySpinner.getSelectedItem();
        String comment = this.comment.getText().toString();

        if (name.isEmpty()) {
            nameTxt.setError("Item name is required!");
            nameTxt.requestFocus();
            return;
        }
        if (this.value.getText().toString().isEmpty()) {
            this.value.setError("Value cannot be empty!");
            this.value.requestFocus();
            return;
        }
        if (shortDescription.isEmpty()) {
            this.shortDescription.setError("Short description is required!");
            this.shortDescription.requestFocus();
            return;
        }
        if (fullDescription.isEmpty()) {
            this.fullDescription.setError("Full description is required!");
            this.fullDescription.requestFocus();
            return;
        }
        if (comment.isEmpty()) {
            comment = "";
        }
        if (hasCamera() && (mPhotoPath == null)) {
            Toast.makeText(AddDonationActivity.this, "Take a picture!",
                    Toast.LENGTH_SHORT).show();
            this.photoView.requestFocus();
            return;
        }

        final Map<String, Object> donation = new HashMap<>();
        donation.put("name", name);
        donation.put("location", location.getName());
        donation.put("value", Double.toString(value));
        donation.put("shortDescription", shortDescription);
        donation.put("fullDescription", fullDescription);
        donation.put("category", category.toString());
        donation.put("comment", comment);
        donation.put("timestamp", FieldValue.serverTimestamp());



        final String nmID = name.replaceAll("[&\\s+]","") +
                "_" + location.getName().replaceAll("[&\\s+]","");

        db.collection("donations")
                .document(nmID)
                .set(donation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddDonationActivity.this,
                                "Added donation to database.",
                                Toast.LENGTH_SHORT).show();
                        if (hasCamera()) {
                            File photoFile = new File(mPhotoPath);
                            Uri photoUri = Uri.fromFile(photoFile);
                            StorageReference filePath =mStorage.child("donationImages").child(nmID);
                            filePath.putFile(photoUri).addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(AddDonationActivity.this,
                                                    "Added image to storage.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent backtoLocationPageIntent =
                                                    new Intent(AddDonationActivity.this,
                                                            LocationPageActivity.class);
                                            backtoLocationPageIntent.putExtra("location", location);
                                            startActivity(backtoLocationPageIntent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddDonationActivity.this,
                                            "Failed to add image to storage.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent backtoLocationPageIntent = new Intent(
                                            AddDonationActivity.this,
                                            LocationPageActivity.class);
                                    backtoLocationPageIntent.putExtra("location", location);
                                    startActivity(backtoLocationPageIntent);
                                }
                            });
                        } else {
                            Intent backtoLocationPageIntent = new Intent(
                                    AddDonationActivity.this,
                                    LocationPageActivity.class);
                            backtoLocationPageIntent.putExtra("location", location);
                            startActivity(backtoLocationPageIntent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDonationActivity.this,
                                "Failed to add donation to database.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        /*db.collection("donations")
                .add(donation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddDonationActivity.this,
                                "Added donation to database.",
                                Toast.LENGTH_SHORT).show();
                        if (hasCamera()) {
                            File photoFile = new File(mPhotoPath);
                            Uri photoUri = Uri.fromFile(photoFile);
                            StorageReference filePath =mStorage.child("donationImages").child(nmID);
                            filePath.putFile(photoUri).addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(AddDonationActivity.this,
                                            "Added image to storage.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent backtoLocationPageIntent =
                                            new Intent(AddDonationActivity.this,
                                            LocationPageActivity.class);
                                    backtoLocationPageIntent.putExtra("location", location);
                                    startActivity(backtoLocationPageIntent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddDonationActivity.this,
                                            "Failed to add image to storage.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent backtoLocationPageIntent = new Intent(
                                            AddDonationActivity.this,
                                            LocationPageActivity.class);
                                    backtoLocationPageIntent.putExtra("location", location);
                                    startActivity(backtoLocationPageIntent);
                                }
                            });
                        } else {
                            Intent backtoLocationPageIntent = new Intent(
                                    AddDonationActivity.this,
                                    LocationPageActivity.class);
                            backtoLocationPageIntent.putExtra("location", location);
                            startActivity(backtoLocationPageIntent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddDonationActivity.this,
                                "Failed to add donation to database.",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    /**
     * Function to check if the device has a camera
     * @return boolean value of existence of camera
     */
    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * Function to launch camera
     * @param view takePhoto button view
     */
    public void launchCamera(View view) {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.team58.fileprovider",
                        photoFile);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK)) {
            File imgFile = new  File(mPhotoPath);
            if(imgFile.exists())            {
                photoView.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }
}
