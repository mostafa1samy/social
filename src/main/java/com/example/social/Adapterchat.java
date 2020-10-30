package com.example.social;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapterchat extends RecyclerView.Adapter<Adapterchat.myholder> {
List<Messagechat>messagechats;
Context context;
FirebaseUser fuser;
String imageuri;
static final int MSG_TYPE_LEFT=0;
    static final int MSG_TYPE_RIGHT=1;

    public Adapterchat(List<Messagechat> messagechats, Context context, String imageuri) {
        this.messagechats = messagechats;
        this.context = context;
        this.imageuri = imageuri;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     if (viewType==MSG_TYPE_LEFT){
         View  view= LayoutInflater.from(context).inflate(R.layout.leftt,parent,false);
         return new myholder(view);
     }

     else
     {
         View  view= LayoutInflater.from(context).inflate(R.layout.rightt,parent,false);
         return new myholder(view);
     }

    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {
String ms=messagechats.get(position).getMessage();

        String da=messagechats.get(position).getTimetamp();
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(da));
          String datatime=DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        holder.messagetv.setText(ms);
        holder.timetv.setText(datatime);
        Picasso.get().load(imageuri).into(holder.protv);
        if (position==messagechats.size()-1){
            if (messagechats.get(position).isseen){
                holder.isseen.setText("seen");
            }
            else{
                holder.isseen.setText("delivered");
            }

        }
        else{
            holder.isseen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messagechats.size();
    }

    @Override
    public int getItemViewType(int position) {
fuser= FirebaseAuth.getInstance().getCurrentUser();
if (messagechats.get(position).getSender().equals(fuser.getUid())){
    return MSG_TYPE_RIGHT;
}
else {
    return MSG_TYPE_LEFT;
}


    }

    class myholder extends RecyclerView.ViewHolder{
CircleImageView protv;
TextView messagetv,timetv,isseen;

        public myholder(@NonNull View itemView) {
            super(itemView);
            protv=itemView.findViewById(R.id.protv);
           messagetv=itemView.findViewById(R.id.messagetv);
          timetv=itemView.findViewById(R.id.timetv);
            isseen=itemView.findViewById(R.id.isseentv);
        }
    }
}
