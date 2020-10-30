package com.example.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
FirebaseAuth  firebaseAuth;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        actionBar=getSupportActionBar();
        actionBar.setTitle("profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
       // actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        firebaseAuth=FirebaseAuth.getInstance();
        BottomNavigationView navigationView=findViewById(R.id.nav);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.hom:
                        actionBar.setTitle("home");
                        home home=new home();
                        FragmentTransaction transaction=getSupportFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.containeer,home,"");
                        transaction.commit();


                        return true;
                    case R.id.per:
                        actionBar.setTitle("Profile");
                      profile profile=new profile();
                        FragmentTransaction transaction1=getSupportFragmentManager()
                                .beginTransaction();
                        transaction1.replace(R.id.containeer,profile,"");
                        transaction1.commit();
                        return true;
                    case R.id.gro:
                        actionBar.setTitle("Users");
                       group  group=new group();
                        FragmentTransaction transaction2=getSupportFragmentManager()
                                .beginTransaction();
                        transaction2.replace(R.id.containeer,group,"");
                        transaction2.commit();
                        return true;
                    case R.id.chat:
                        actionBar.setTitle("Chat");
                        chat chat=new chat();
                        FragmentTransaction transaction3=getSupportFragmentManager()
                                .beginTransaction();
                        transaction3.replace(R.id.containeer,chat,"");
                        transaction3.commit();
                        return true;



                }
                return false;
            }
        });

    }



    private void checkstatauser() {
        FirebaseUser  user=firebaseAuth.getCurrentUser();
        if (user !=null){

        }
        else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
       // onBackPressed();
        return super.onSupportNavigateUp();
    }
}
