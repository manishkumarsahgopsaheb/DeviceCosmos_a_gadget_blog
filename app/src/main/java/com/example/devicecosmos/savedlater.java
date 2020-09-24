package com.example.devicecosmos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class savedlater extends AppCompatActivity {
DatabaseReference databaseReference;
RecyclerView saverecycle;
FirebaseAuth mAuth;
String Uid;
String currentemail;
TextView saveref;
    String  str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedlater);

         saveref = (TextView)findViewById(R.id.click);

        saverecycle = (RecyclerView) findViewById(R.id.savedrecycle_id);
        saverecycle.setHasFixedSize(true);
        saverecycle.setLayoutManager(new LinearLayoutManager(this));




        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();
        currentemail = mAuth.getCurrentUser().getEmail();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid);
        databaseReference.keepSynced(true);

        callsave();
    }

    private void callsave() {
        FirebaseRecyclerAdapter<savereference, saveholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<savereference, saveholder>
                (savereference.class, R.layout.savedrecycle, saveholder.class, databaseReference) {
            @Override
            protected void populateViewHolder(saveholder viewHolder, savereference model, int position) {
                viewHolder.setHeading(model.getHeading());
                viewHolder.setPhoto(model.getPhoto());

                final String userid = getRef(position).getKey();

                ImageView deleteimg = (ImageView) viewHolder.itemView.findViewById(R.id.delete_id);


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),blogdetails.class);
                        intent.putExtra("fetchblog_id",userid);
                        startActivity(intent);
                    }
                });

                deleteimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(savedlater.this);
                        builder.setMessage("You want to delete this blog?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {

                                        // DatabaseReference deleteblog = FirebaseDatabase.getInstance().getReference("Blogs").child(userid);
                                        DatabaseReference deleteblog = databaseReference.child(userid);
                                        deleteblog.removeValue();
                                        Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                // ImageView like = (ImageView)viewHolder.itemView.findViewById(R.id.like_id);

              /*  like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int sum = k + 1;
                        TextView intcount = (TextView)findViewById(R.id.integer_id);
                        intcount.setText(sum);
                    }
                });
                */
            }

        };

        saverecycle.setAdapter(firebaseRecyclerAdapter);
    }

    public static class saveholder extends RecyclerView.ViewHolder
    {
        View myview;

        public saveholder(View itemView) {
            super(itemView);
            myview = itemView;

        }



        public void setHeading(String heading){
            TextView mess = (TextView)myview.findViewById(R.id.message_id);
            mess.setText(heading);
        }

        public void setPhoto(String photo){
            ImageView imgv = (ImageView)myview.findViewById(R.id.postimage_id);
            Picasso.get().load(photo).into(imgv);
        }

    }


}
