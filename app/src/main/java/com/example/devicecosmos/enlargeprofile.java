package com.example.devicecosmos;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class enlargeprofile extends AppCompatActivity {
ImageView enlargeimage;
TextView nametext;
DatabaseReference databaseReference;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlargeprofile);

        enlargeimage = (ImageView)findViewById(R.id.enlargeprof_id);
        nametext = (TextView)findViewById(R.id.nametext_id);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users_details");

        String uid = mAuth.getCurrentUser().getUid();

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name")) && (dataSnapshot.hasChild("image")))
                {
                    String imageurl = dataSnapshot.child("image").getValue().toString();
                    String name = dataSnapshot.child("Name").getValue().toString();

                    Picasso.get().load(imageurl).into(enlargeimage);
                    nametext.setText(name);

                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name")))
                {
                    String nam = dataSnapshot.child("Name").getValue().toString();
                    nametext.setText(nam);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
