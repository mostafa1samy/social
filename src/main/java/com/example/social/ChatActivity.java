package com.example.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
RecyclerView recyclerView;
Toolbar toolbar;
CircleImageView profilttv;
FirebaseAuth firebaseAuth;
TextView His_name,stata;
EditText message;
ValueEventListener seenlisnter;
DatabaseReference userrefforseen;
List<Messagechat>list;
Adapterchat adapterchat;
ImageButton send;
String his_id;
String my_id;
String hisimage;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.rec);
        LinearLayoutManager  manager=new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
         profilttv=findViewById(R.id.profiletv);
        His_name=findViewById(R.id.his_name);
        firebaseAuth=FirebaseAuth.getInstance();
        stata=findViewById(R.id.his_stata);

        send=findViewById(R.id.send);
        message=findViewById(R.id.message);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

         Intent intent=getIntent();
         his_id=intent.getStringExtra("mos");

        Query query=databaseReference.orderByChild("id").equalTo(his_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           for (DataSnapshot ds:dataSnapshot.getChildren()){
               String n=""+ds.child("name").getValue();
               hisimage=""+ds.child("image").getValue();
               His_name.setText(n);
               try {
                   Picasso.get().load(hisimage).placeholder(R.drawable.ic_face_black_24dp).into(profilttv);
               }
               catch (Exception e){
                   Picasso.get().load(R.drawable.ic_face_black_24dp).into(profilttv);

               }
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mass=message.getText().toString().trim();
                if (!TextUtils.isEmpty(mass)){
                     sendmessage(mass);

                }
                else{
                    Toast.makeText(ChatActivity.this, "please enter messge", Toast.LENGTH_SHORT).show();
                }
            }
        });


readMessage();
seenmessage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userrefforseen.removeEventListener(seenlisnter);
    }

    private void seenmessage() {
        userrefforseen=FirebaseDatabase.getInstance().getReference("Chats");
        seenlisnter=userrefforseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         for (DataSnapshot ds:dataSnapshot.getChildren()){

             Messagechat messagechat=ds.getValue(Messagechat.class);
             if (messagechat.getSender().equals(his_id)&&messagechat.getReceiver().equals(my_id)){
                 HashMap<String,Object>h=new HashMap<>();
                 h.put("isSeen",true);
                 ds.getRef().updateChildren(h);
             }
         }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        list=new ArrayList<>();
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
list.clear();
      for (DataSnapshot ds:dataSnapshot.getChildren()){
          Messagechat messagechat=ds.getValue(Messagechat.class);
          if (messagechat.getReceiver().equals(my_id)&& messagechat.getSender().equals(his_id)||
                  messagechat.getReceiver().equals(his_id)&& messagechat.getSender().equals(my_id)){
              list.add(messagechat);
          }
          adapterchat=new Adapterchat(list,ChatActivity.this,hisimage);
          adapterchat.notifyDataSetChanged();
          recyclerView.setAdapter(adapterchat);





      }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void  checkstatauser() {
        FirebaseUser fuser=firebaseAuth.getCurrentUser();
        my_id=fuser.getUid();
        if (fuser !=null){


        }
        else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

            finish();
        }
    }

    @Override
    protected void onStart() {
        checkstatauser();
        super.onStart();
    }

    private void sendmessage(String mass) {
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference();
       String t=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object>map=new HashMap<>();
        map.put("sender",my_id);
        map.put("receiver",his_id);
        map.put("message",mass);
        map.put("timestamp",t);
        map.put("isSeen",false);
        databaseReference1.child("Chats").push().setValue(map);
        message.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.men,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.lo){
            firebaseAuth.signOut();
            checkstatauser();

        }

        return super.onOptionsItemSelected(item);
    }
}
