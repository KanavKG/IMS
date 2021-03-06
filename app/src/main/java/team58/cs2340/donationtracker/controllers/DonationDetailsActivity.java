package team58.cs2340.donationtracker.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import team58.cs2340.donationtracker.models.Category;
import team58.cs2340.donationtracker.models.CurrUserLocal;
import team58.cs2340.donationtracker.models.Donation;
import team58.cs2340.donationtracker.R;
import team58.cs2340.donationtracker.models.Role;

/**
 * Class for donation details activity
 */
public class DonationDetailsActivity extends AppCompatActivity {

    private ImageView photoView;
    private Bitmap donationImage;
    private Donation donation;
    private int iconID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_item_detail);

        TextView name = findViewById(R.id.name);
        TextView location = findViewById(R.id.location);
        TextView shortDescription = findViewById(R.id.shortDescription);
        TextView fullDescription = findViewById(R.id.fullDescription);
        TextView value = findViewById(R.id.value);
        TextView comment = findViewById(R.id.comment);
        TextView timeStamp = findViewById(R.id.timeStamp);
        CurrUserLocal userManager = CurrUserLocal.getInstance();
        Button editBtn = findViewById(R.id.editBtn);
        this.photoView = findViewById(R.id.photoView);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        TextView catTxt = findViewById(R.id.category);

        final Button request = findViewById(R.id.request);

        Intent intent = getIntent();
        donation  = (Donation) intent.getSerializableExtra("donation");
        final Serializable locationExtra  = intent.getSerializableExtra("location");

        //assert userManager.getCurrentUser() != null;
        if (((userManager.getCurrentUserRole() == Role.LOCATIONEMPLOYEE) && userManager.
                getCurrentUserLocation().equals(donation.getLocation()))) {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addItemIntent = new Intent(
                            DonationDetailsActivity.this, EditDonationActivity.class);
                    addItemIntent.putExtra("donation", donation);
                    addItemIntent.putExtra("location", locationExtra);
                    startActivity(addItemIntent);
                }
            });
        } else {
            editBtn.setVisibility(View.GONE);
        }

        if (((userManager.getCurrentUserRole() == Role.USER))) {
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.setText("Requested");
                    request.getBackground()
                            .setColorFilter(new LightingColorFilter(
                                    0xFFFFFFFF, 0xFFAA0000));
                }
            });
        } else {
            request.setVisibility(View.GONE);
        }

        final Category cat = donation.getCategory();

        name.setText(donation.getName());
        catTxt.setText(cat.toString());
        location.setText(donation.getLocation());
        String val = "$" + donation.getValue();
        value.setText(val);
        String shortDes = "Short Description: " + donation.getShortDescription();
        shortDescription.setText(shortDes);
        String fullDes = "Full Description: " + donation.getFullDescription();
        fullDescription.setText(fullDes);
        String com = "Comment: " + donation.getComment();
        comment.setText(com);
        String time = "Time Stamp: " + donation.getTimeStamp();
        timeStamp.setText(time);

        final String nmID = donation.getName().replaceAll("[&\\s+]","") +
                "_" + donation.getLocation().replaceAll("[&\\s+]","");

        StorageReference pathReference = mStorageRef.child("donationImages/" + nmID);

        try {
            final File localFile = File.createTempFile("donationImages", "jpg");
            pathReference.getFile(localFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    donationImage = BitmapFactory.decodeFile(localFile.getAbsolutePath(), options);
                    photoView.setImageBitmap(donationImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    switch (cat) {
                        case APPLIANCES:
                            iconID = R.drawable.ic_1appliances;
                            break;
                        case BABY:
                            iconID = R.drawable.ic_1baby;
                            break;
                        case BAGSANDACCESSORIES:
                            iconID = R.drawable.ic_1bags;
                            break;
                        case BOOKSANDMUSIC:
                            iconID = R.drawable.ic_1books;
                            break;
                        case CLOTHING:
                            iconID = R.drawable.ic_1clothing;
                            break;
                        case ELECTRONICS:
                            iconID = R.drawable.ic_1electronics;
                            break;
                        case FOOD:
                            iconID = R.drawable.ic_1food;
                            break;
                        case FURNITURE:
                            iconID = R.drawable.ic_1furniture;
                            break;
                        case MOVIESANDGAMES:
                            iconID = R.drawable.ic_1movie;
                            break;
                        case SPORTSANDOUTDOORS:
                            photoView.setImageResource(R.drawable.ic_1sports);
                            break;
                        case TOYS:
                            iconID = R.drawable.ic_1toys;
                            break;
                    }
                    photoView.setImageResource(iconID);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

