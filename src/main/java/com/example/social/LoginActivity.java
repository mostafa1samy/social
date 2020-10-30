package com.example.social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText email,pass;
    ProgressDialog dialog;
    FirebaseAuth auth;
    String email_l,pass_L;
    SignInButton signInButton;
    static final int RC_SIGN_IN=100;
    GoogleSignInClient mgoogleSignInClient;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         actionBar=getSupportActionBar();
        actionBar.setTitle("login");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        email=findViewById(R.id.email_l);
        pass=findViewById(R.id.pass_l);
        signInButton=findViewById(R.id.g);
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                .build();
        mgoogleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(LoginActivity.this);
        dialog.setMessage("wait...");
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=mgoogleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);

            }
        });
    }

    public void llogin(View view) {

        email_l=email.getText().toString().trim();
        pass_L=pass.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email_l).matches()){
            email.setFocusable(true);
            email.setError("invaild");
        }
        dialog.show();
        loginn(email_l,pass_L);

    }

    private void loginn(String email_l, String pass_l) {

        auth.signInWithEmailAndPassword(email_l,pass_l).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();
                    String emai=user.getEmail();
                    String id=user.getUid();
                    HashMap<Object,String> hashMap=new HashMap<>();
                    hashMap.put("email",emai);
                    hashMap.put("id",id);
                    hashMap.put("phone","");
                    hashMap.put("name","");
                    hashMap.put("image","");
                    hashMap.put("cover","");
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference reference=database.getReference("Users");
                    reference.child(id).setValue(hashMap);
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void not(View view) {
        startActivity(new Intent(getApplicationContext(),RegisActivity.class));
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

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        //onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseauthwithgoogle(account);
                
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseauthwithgoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getAdditionalUserInfo().isNewUser()){
                        FirebaseUser user=auth.getCurrentUser();
                        String emai=user.getEmail();
                        String id=user.getUid();
                        HashMap<Object,String> hashMap=new HashMap<>();
                        hashMap.put("email",emai);
                        hashMap.put("id",id);
                        hashMap.put("phone","");
                        hashMap.put("name","");
                        hashMap.put("image","");
                        hashMap.put("cover","");
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference reference=database.getReference("Users");
                        reference.child(id).setValue(hashMap);
                    }
                    Toast.makeText(LoginActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void recover(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("recover  password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText editText=new EditText(this);
        editText.setHint("Email");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(editText);
        linearLayout.setPadding(10,10,10,10);
        editText.setMinEms(10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
String str=editText.getText().toString().trim();
beginrecover(str);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginrecover(String str) {
        dialog.show();
        auth.sendPasswordResetEmail(str).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
if (task.isSuccessful()){
    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
}
else{
    Toast.makeText(LoginActivity.this, "fail...", Toast.LENGTH_SHORT).show();

}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
