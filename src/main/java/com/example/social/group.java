package com.example.social;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class group extends Fragment {

View view;
FirebaseAuth auth;
Adapteruser adapteruser;
List<User> users;
RecyclerView recyclerView;
    public group() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view=inflater.inflate(R.layout.fragment_group, container, false);
auth=FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.user_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       users=new ArrayList<>();
       getalluser();
        initSearch();


        return view;

    }



    private void getalluser() {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       users.clear();
  for (DataSnapshot ds:dataSnapshot.getChildren()){
      User user=ds.getValue(User.class);
      if (!(user.getId()==firebaseUser.getUid())){
          users.add(user);
      }
      adapteruser=new Adapteruser(getActivity(),users);
      recyclerView.setAdapter(adapteruser);


  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
    private void initSearch()
    {
        SearchView searchView = view.findViewById(R.id.searchview);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapteruser.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapteruser.getFilter().filter(newText);
                return false;
            }
        });
    }

}
