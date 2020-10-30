package com.example.social;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapteruser extends RecyclerView.Adapter<Adapteruser.Myholder> implements Filterable {
 //   String his_id;
Context context;
List<User>userList;
    List<User> filterddoctorModels;

    public Adapteruser(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.filterddoctorModels = new ArrayList<>(userList);

    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user,parent,false);



        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
      final String s=userList.get(position).getId();
        String userimage=userList.get(position).getImage();

        String username=userList.get(position).getName();
        final String useremail=userList.get(position).getEmail();
        holder.name.setText(username);
        holder.email.setText(useremail);
        try{
            Picasso.get().load(userimage).placeholder(R.drawable.ic_person_black_24dp).into(holder.circleImageView);
        }
        catch (Exception e){
                  }

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {



        Intent intent=new Intent(context,ChatActivity.class);
      intent.putExtra("mos",s);
      context.startActivity(intent);

       //Toast.makeText(context, ""+user.getUid(), Toast.LENGTH_SHORT).show();


    }
});
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    private Filter exampleFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(filterddoctorModels);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User item : filterddoctorModels)
                {
                    if (item.getName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



    class Myholder extends RecyclerView.ViewHolder{
    CircleImageView circleImageView;
    TextView name,email;

    public Myholder(@NonNull View itemView) {
        super(itemView);

        circleImageView=itemView.findViewById(R.id.cir);
        name=itemView.findViewById(R.id.pname);
        email=itemView.findViewById(R.id.pemail);
    }
}

}
