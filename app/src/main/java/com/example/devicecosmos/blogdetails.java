package com.example.devicecosmos;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class blogdetails extends AppCompatActivity {
private String receiveblogid;
private DatabaseReference databaseReference , mDatabaseLikeCount ,databasechildcount ,databsesaveditem;
ImageView retrieveblogimage;
TextView retrieveheading;
TextView retrievetime;
TextView retrievedate;
TextView retrievedescription;
    //String link;
  private   ImageView linkimg,likeintent,saveintent;
  TextView counttext;
  Boolean mprocesslike = false;
  Boolean mProcesssave = false;
  FirebaseAuth mAuth;
  String Uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogdetails);

        retrieveblogimage = (ImageView)findViewById(R.id.retrievephoto_id);
        retrieveheading = (TextView)findViewById(R.id.retrieveheading_id);
        retrievetime = (TextView)findViewById(R.id.retrievetime_id);
        retrievedate = (TextView)findViewById(R.id.retrievedate_id);
        retrievedescription = (TextView)findViewById(R.id.retrievedescription_id);
        linkimg = (ImageView)findViewById(R.id.intentbrowser_id);
        likeintent = (ImageView)findViewById(R.id.likeintent_id);
        saveintent = (ImageView)findViewById(R.id.saveintent_id);
        counttext = (TextView)findViewById(R.id.countintent_id);

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();

        receiveblogid = getIntent().getExtras().get("fetchblog_id").toString();



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blogs").child(receiveblogid);

        linkimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intenturl();
            }
        });

        likeintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentlike();
            }
        });
        saveintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savelater();
            }
        });

    }

    private void savelater()
    {
        mProcesssave = true;
        databsesaveditem = FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid);
        //.child(post_key);
        // datarejct = FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid);
        databsesaveditem.keepSynced(true);

        databsesaveditem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (mProcesssave)
                {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild(receiveblogid))
                    {
                        // considered as already saved item
                        Toast.makeText(getApplicationContext(),"saved item removed",Toast.LENGTH_SHORT).show();
                        databsesaveditem.child(receiveblogid).removeValue();
                        saveintent.setImageResource(R.drawable.save);
                        mProcesssave = false;

                    }
                    else
                    {
                        // save the item
                        Toast.makeText(getApplicationContext(),"saved for later",Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("Blogs").child(receiveblogid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("photo")))
                                {
                                    String  retrieveprofimg = dataSnapshot.child("profilephoto").getValue().toString();
                                    String retrieveusername = dataSnapshot.child("username").getValue().toString();
                                    String retrieveuploadphoto = dataSnapshot.child("photo").getValue().toString();
                                    String retrieveheading = dataSnapshot.child("heading").getValue().toString();

                                    HashMap retrievesaving = new HashMap<>();
                                    retrievesaving.put("profilephoto",retrieveprofimg);
                                    retrievesaving.put("username",retrieveusername);
                                    retrievesaving.put("photo",retrieveuploadphoto);
                                    retrievesaving.put("heading",retrieveheading);


                                    databsesaveditem.child(receiveblogid).setValue(retrievesaving);
                                    saveintent.setImageResource(R.drawable.saved);
                                    mProcesssave = false;

                                }
                                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("heading")))
                                {
                                    String  retrieveprofimg = dataSnapshot.child("profilephoto").getValue().toString();
                                    String  retrieveusername = dataSnapshot.child("username").getValue().toString();
                                    String retrieveheading = dataSnapshot.child("heading").getValue().toString();

                                    HashMap retrievesaving = new HashMap<>();
                                    retrievesaving.put("profilephoto",retrieveprofimg);
                                    retrievesaving.put("username",retrieveusername);
                                    retrievesaving.put("heading",retrieveheading);


                                    databsesaveditem.child(receiveblogid).setValue(retrievesaving);
                                    saveintent.setImageResource(R.drawable.saved);
                                    mProcesssave = false;

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void intentlike()
    {
        { mprocesslike = true;

        // there is something bug in like count code which caused to intently call browser
        //  lovedimg.setVisibility(View.VISIBLE);

        mDatabaseLikeCount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(receiveblogid).child("likecount");
        databasechildcount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(receiveblogid).child("count");
        mDatabaseLikeCount.keepSynced(true);
        //Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_LONG).show();

        mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (mprocesslike)
                { //int Count = 0;
                    if (dataSnapshot.exists() && dataSnapshot.hasChild(Uid)) {
                        Log.i("D Diary", "User has already Liked. So it can be considered as Unliked.");
                        mDatabaseLikeCount.child(Uid).removeValue();

                        long likes = dataSnapshot.getChildrenCount();
                        long lk = likes - 1;
                        String likecount  = Long.toString(lk);
                        //databasechildcount.setValue(dataSnapshot.getChildrenCount() - 1).toString();


                        //lovedimg.setVisibility(View.INVISIBLE);

                        databasechildcount.setValue(likecount);
                        //updateCounter(false);
                        //like.setBackground(Color.YELLOW);
                        mprocesslike = false;
                    } else {
                        Log.i("D Diary", "User Liked");
                        mDatabaseLikeCount.child(Uid).setValue(mAuth.getCurrentUser().getDisplayName());

                        long likes = dataSnapshot.getChildrenCount() + 1;
                        String likecount  = Long.toString(likes);

                        databasechildcount.setValue(likecount);

                        //databasechildcount.setValue(dataSnapshot.getChildrenCount() + 1).toString();
                        //updateCounter(true);
                        //Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                        mprocesslike = false;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
    }
    }

    private void intenturl() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Urllink")))
                {
                    String intenttolink = dataSnapshot.child("Urllink").getValue().toString();

                    if (intenttolink.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"link not available",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri url = Uri.parse(intenttolink);
                        intent.setData(url);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("photo").getValue().toString();
                    String heading = dataSnapshot.child("heading").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String count = dataSnapshot.child("count").getValue().toString();
                    // final String link = dataSnapshot.child("link").getValue().toString();

                    Picasso.get().load(image).into(retrieveblogimage);
                    retrieveheading.setText(heading);
                    retrievetime.setText(time);
                    retrievedate.setText(date);
                    retrievedescription.setText(description);
                    counttext.setText(count);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("SavedItem").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(receiveblogid)))
                {
                    saveintent.setImageResource(R.drawable.saved);
                }
                else
                    {
                        saveintent.setImageResource(R.drawable.save);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
