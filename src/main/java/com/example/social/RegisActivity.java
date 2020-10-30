package com.example.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisActivity extends AppCompatActivity {
    ProgressDialog dialog;
    EditText email,pass;
    String email_txt,pass_txt;

    FirebaseAuth  auth;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        actionBar=getSupportActionBar();
      actionBar.setTitle("create account");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        email=findViewById(R.id.emai_f);
        auth=FirebaseAuth.getInstance();

        pass=findViewById(R.id.pass_f);
        dialog=new ProgressDialog(RegisActivity.this);
        dialog.setMessage("wait....");






    }

    @Override
    public boolean onSupportNavigateUp() {
       // onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        return super.onSupportNavigateUp();
    }

    public void rr(View view) {

        email_txt=email.getText().toString().trim();
        pass_txt=pass.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email_txt).matches()){
            email.setError("Invaild");
            email.setFocusable(true);
            return;
        }
        if (pass_txt.length()<6){
            pass.setError("password at least 6 character");
            pass.setFocusable(true);
            return;
        }

        creatuser(email_txt,pass_txt);
    }

    private void creatuser(String email_txt, String pass_txt) {
        dialog.show();
        auth.createUserWithEmailAndPassword(email_txt,pass_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    dialog.dismiss();
                    FirebaseUser user=auth.getCurrentUser();

                    String emai=user.getEmail();
                    String id=user.getUid();
                    HashMap<Object,String>hashMap=new HashMap<>();
                    hashMap.put("email",emai);
                    hashMap.put("id",id);
                    hashMap.put("phone","");
                    hashMap.put("name","");
                    hashMap.put("image","");
                    hashMap.put("cover","");
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference  reference=database.getReference("Users");
                    reference.child(id).setValue(hashMap);

                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(RegisActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(RegisActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }


    public void tv(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.men,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkstatauser() {
        FirebaseUser  user=auth.getCurrentUser();
        if (user !=null){

        }
        else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.lo){
            auth.signOut();
            checkstatauser();

        }

        return super.onOptionsItemSelected(item);
    }
}
