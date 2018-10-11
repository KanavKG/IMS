package team58.cs2340.donationtracker.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import team58.cs2340.donationtracker.R;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.loginBtn:
                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);
                break;
            case R.id.signupBtn:
                Intent registrationIntent = new Intent(this, Registration.class);
                startActivity(registrationIntent);
                Toast.makeText(Welcome.this, "Register", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}