package com.example.social;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class profile extends Fragment {
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    ImageView imageView,covertv;
    TextView nametv,phonetv,emailtv;
    FloatingActionButton floatac;
View view;
private static final int CAMRA_REQUEST_COOD=100;
    private static final int STORAGE_REQUEST_COOD=200;
    private static final int IMAGE_PICK_CAMRA_COOD=400;
    private static final int IMAGE_PICK_GALLARY_COOD=300;
String camarapermission[];
String storagepermission[];
Uri uri_image;
String profileorcoverphoto;
StorageReference storageReference;
String storagepath="Users_profile_cover_images/";
    public profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view= inflater.inflate(R.layout.fragment_profile, container, false);
       auth=FirebaseAuth.getInstance();
       user=auth.getCurrentUser();
       database=FirebaseDatabase.getInstance();
       reference=database.getReference("Users");
       storageReference=getInstance().getReference();
       imageView=view.findViewById(R.id.imag);
       covertv=view.findViewById(R.id.covertv);
       nametv=view.findViewById(R.id.nametv);
       phonetv=view.findViewById(R.id.phonetv);
       emailtv=view.findViewById(R.id.emailtv);
       floatac=view.findViewById(R.id.fab);



       camarapermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
       storagepermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
       progressDialog=new ProgressDialog(getActivity());
        Query query=reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String image=""+ds.child("image").getValue();
                    String cover=""+ds.child("cover").getValue();
                    nametv.setText(name);
                    phonetv.setText(phone);
                    emailtv.setText(email);
                    try{
                        Picasso.get().load(image).into(imageView);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_add_a_photo_black_24dp).into(imageView);
                    }
                    try{
                        Picasso.get().load(cover).into(covertv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_add_a_photo_black_24dp).into(covertv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        floatac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showeditprofilediaolog();
            }
        });




       return view;
    }

    private void showeditprofilediaolog() {
        String options[]={"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Phone"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){

                    progressDialog.setMessage("Updating Profile Picture");
                    profileorcoverphoto="image";
                    showimagepicdiolog();

                }
                else if (i==1){
                    progressDialog.setMessage("Updating Cover Photo");
                    profileorcoverphoto="cover";
                    showimagepicdiolog();
                }
                else if (i==2){
                    progressDialog.setMessage("Updating Name");
                    shownamephoneupdatadialoldg("name");

                }
                else if (i==3){
                    progressDialog.setMessage("Updating Phone");
                    shownamephoneupdatadialoldg("phone");

                }
            }
        });
        builder.create().show();
    }

    private void shownamephoneupdatadialoldg(final  String name) {
AlertDialog.Builder  builder1=new AlertDialog.Builder(getActivity());
builder1.setTitle("Updata "+name);
        LinearLayout   layout=new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10,10,10,10 );
        final EditText text=new EditText(getActivity());
        text.setHint("Enter"+name);
        layout.addView(text);
        builder1.setView(layout);
        builder1.setPositiveButton("Updata", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value=text.getText().toString().trim();
                 if (!TextUtils.isEmpty(value)){
                     progressDialog.show();
                     HashMap<String,Object> result=new HashMap<>();
                     result.put(name,value);
                     reference.child(user.getUid()).updateChildren(result)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     progressDialog.dismiss();
                                     Toast.makeText(getActivity(), "Updata...", Toast.LENGTH_SHORT).show();
                                     
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             progressDialog.dismiss();
                             Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });

                 }
                 else{
                     Toast.makeText(getActivity(), "Enter "+name+"", Toast.LENGTH_SHORT).show();
                 }

            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder1.create().show();



    }

    private boolean checkstorgepermission(){
        boolean res= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return res;
    }
    private void requsetstoragepermission(){
       requestPermissions(storagepermission,STORAGE_REQUEST_COOD);
    }
    private boolean checkcamarpermission(){
        boolean res1= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean res= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return res&&res1;
    }
    private void requsetcamrapermission(){
      requestPermissions(camarapermission,CAMRA_REQUEST_COOD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     switch (requestCode){
         case CAMRA_REQUEST_COOD:{
             if (grantResults.length>0){
                 boolean carmeaaccept=grantResults[0]==PackageManager
                         .PERMISSION_GRANTED;
                 boolean writestorageaccept=grantResults[1]==PackageManager
                         .PERMISSION_GRANTED;
                 if (carmeaaccept&&writestorageaccept){
                     pickfromcamera();

                 }
                 else{
                     Toast.makeText(getActivity(), "Please enable camera and storage", Toast.LENGTH_SHORT).show();
                 }
             }
         }
         break;
         case STORAGE_REQUEST_COOD:{
             if (grantResults.length>0){
                 boolean writestorageacceptt=grantResults[1]==PackageManager
                         .PERMISSION_GRANTED;
                 if (writestorageacceptt){
                     pickfromgallery();
                     
                 }
                 else{
                     Toast.makeText(getActivity(), "please enable storage", Toast.LENGTH_SHORT).show();
                 }
                 
                 
                 
                 
                 
             }
             
             

         }
         break;
     }
     

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.men,menu);




        super.onCreateOptionsMenu(menu, inflater);
    }
    private void checkstatauser() {
        FirebaseUser  user=auth.getCurrentUser();
        if (user !=null){

        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));

            getActivity().finish();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if (resultCode==RESULT_OK){

           if (requestCode==IMAGE_PICK_GALLARY_COOD){
               uri_image=data.getData();
               uploadprofilecoverphoto(uri_image);
           }
           if (requestCode==IMAGE_PICK_CAMRA_COOD){
//               uri_image=data.getData();
               uploadprofilecoverphoto(uri_image);
           }




       }




        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadprofilecoverphoto(Uri uri_image) {
        progressDialog.show();
       String filepathandname=storagepath+""+profileorcoverphoto+"_"+user.getUid();
       StorageReference storageReference1=storageReference.child(filepathandname);
       storageReference1.putFile(uri_image)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isSuccessful());
                       Uri download=uriTask.getResult();
                       if (uriTask.isSuccessful()){
                           HashMap<String,Object>results=new HashMap<>();
                           results.put(profileorcoverphoto,download.toString());
                           reference.child(user.getUid()).updateChildren(results)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           progressDialog.dismiss();
                                           Toast.makeText(getActivity(), "Image Updata", Toast.LENGTH_SHORT).show();

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   progressDialog.dismiss();
                                   Toast.makeText(getActivity(), "Image  not Updata", Toast.LENGTH_SHORT).show();

                               }
                           });

                       }
                       else{
                           progressDialog.dismiss();
                           Toast.makeText(getActivity(), "some erro", Toast.LENGTH_SHORT).show();
                       }

                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               progressDialog.dismiss();
               Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

           }
       });


    }

    private void pickfromgallery() {
        Intent intentgallary=new Intent(Intent.ACTION_PICK);
        intentgallary.setType("image/*");
        startActivityForResult(intentgallary,IMAGE_PICK_GALLARY_COOD);
    }

    private void pickfromcamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        uri_image=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intentcamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentcamera.putExtra(MediaStore.EXTRA_OUTPUT,uri_image);
        startActivityForResult(intentcamera,IMAGE_PICK_CAMRA_COOD);
    }

    private void showimagepicdiolog() {
        String options[]={"Camar","Gallary"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkcamarpermission()){
                        requsetcamrapermission();
                    }
                    else{
                        pickfromcamera();
                    }


                } else if (i == 1) {
                    if (!checkstorgepermission()){
                        requsetstoragepermission();
                    }
                    else {
                        pickfromgallery();
                    }


                }
            }
        });
        builder.create().show();

    }

}
