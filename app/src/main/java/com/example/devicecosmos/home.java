package com.example.devicecosmos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "home";
    ImageView profilelogo;
ImageView deal;
ImageView notify;
ImageView search;
ImageView category;
FirebaseAuth mAuth;
DatabaseReference databaseReferenceofusers,databaseReferenceforblogs,databsesaveditem,mDatabaseLikeCount,databasechildcount;
String Uid;
private RecyclerView blogrecycle;
Boolean mProcessLike = false;
Boolean mProcesssave = false;
ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar)findViewById(R.id.progress_id);
        // for recycle view


        blogrecycle = (RecyclerView) findViewById(R.id.recycleinhome_id);
        blogrecycle.setHasFixedSize(true);
        blogrecycle.setLayoutManager(new LinearLayoutManager(this));
       // blogrecycle.getLayoutManager().setMeasurementCacheEnabled(false);


        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();

        databaseReferenceofusers = FirebaseDatabase.getInstance().getReference().child("users_details");

        databaseReferenceforblogs = FirebaseDatabase.getInstance().getReference().child("Blogs");
        databaseReferenceforblogs.keepSynced(true);

        databsesaveditem = FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid);
        databsesaveditem.keepSynced(true);


        notify = (ImageView)findViewById(R.id.notification_id);
        profilelogo = (ImageView)findViewById(R.id.profile_id);
        deal = (ImageView)findViewById(R.id.deal_id);
        search = (ImageView)findViewById(R.id.search_id);
        category = (ImageView)findViewById(R.id.category_id);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),category.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, search.class);
                startActivity(intent);
            }
        });

        deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,deal.class);
                startActivity(intent);
            }
        });

        profilelogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,profile.class);
                startActivity(intent);
            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,notifications.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // for header
        View headview = navigationView.getHeaderView(0);
        final CircleImageView imageView = headview.findViewById(R.id.imageView);
        TextView nametxt = headview.findViewById(R.id.usernametext_id);

        String nam = mAuth.getCurrentUser().getDisplayName();
        nametxt.setText(nam);

        String imgurl = mAuth.getCurrentUser().getPhotoUrl().toString();

        Picasso.get().load(imgurl).into(imageView);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Query query = databaseReferenceforblogs.orderByChild().equalTo(currentemail);


       // Query query = databaseReferenceforblogs.child("blogcounting").limitToLast(3);
        Query query = databaseReferenceforblogs.orderByChild("blogcounting");
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<blogrefernceforrecycle, BloggettingHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blogrefernceforrecycle, BloggettingHolder>
                (blogrefernceforrecycle.class, R.layout.blogrecycle, BloggettingHolder.class, query) {
            @Override
            protected void populateViewHolder(final BloggettingHolder viewHolder, blogrefernceforrecycle model, int position) {
                viewHolder.setHeading(model.getHeading());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setPhoto(model.getPhoto());
                viewHolder.setCount(model.getCount());


                progressBar.setVisibility(View.GONE);
               // final String post_key = getRef(position).getKey();


                final String post_key = getRef(position).getKey();
                // post_key will give the key from which it is belong to
//



                //final ImageView like = (ImageView)viewHolder.itemView.findViewById(R.id.like_id);
                //final ImageView saveditems = (ImageView)viewHolder.itemView.findViewById(R.id.saved_id);

                // retrieving save color
                databsesaveditem.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                      {
                          if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(post_key)))
                          {
                              viewHolder.saveditems.setImageResource(R.drawable.saved);
                          }
                          else
                              {
                                  viewHolder.saveditems.setImageResource(R.drawable.save);
                              }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(),blogdetails.class);
                        intent.putExtra("fetchblog_id",post_key);
                        startActivity(intent);
                    }
                });

                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        mDatabaseLikeCount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(post_key).child("likecount");
                        // mDatabaseLikeCount = FirebaseDatabase.getInstance().getReference().child("likes").child(post_key);
                        mDatabaseLikeCount.keepSynced(true);

                        databasechildcount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(post_key).child("count");
                        databasechildcount.keepSynced(true);

                        mProcessLike = true;

                        mDatabaseLikeCount.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (mProcessLike)
                                { //int Count = 0;
                                    if (dataSnapshot.exists() && dataSnapshot.hasChild(Uid)) {
                                        Log.i("D Diary", "User has already Liked. So it can be considered as Unliked.");
                                        mDatabaseLikeCount.child(Uid).removeValue();

                                        long likes = dataSnapshot.getChildrenCount();
                                        long lk = likes - 1;
                                        String likecount  = Long.toString(lk);

                                        databasechildcount.setValue(likecount);

                                        //viewHolder.like.setImageResource(R.drawable.like);
                                        mProcessLike = false;
                                    }
                                    else {
                                        Log.i("D Diary", "User Liked");
                                        mDatabaseLikeCount.child(Uid).setValue(mAuth.getCurrentUser().getDisplayName());

                                        long likes = dataSnapshot.getChildrenCount() + 1;
                                        String likecount  = Long.toString(likes);

                                        databasechildcount.setValue(likecount);

                                        //databasechildcount.setValue(dataSnapshot.getChildrenCount() + 1).toString();
                                        //updateCounter(true);
                                        //Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                                        //viewHolder.like.setImageResource(R.drawable.love);
                                        mProcessLike = false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //
                    }
                });


                viewHolder.saveditems.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        mProcesssave = true;

                        databsesaveditem.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (mProcesssave)
                                {
                                    if (dataSnapshot.exists() && dataSnapshot.hasChild(post_key))
                                    {
                                        // considered as already saved item
                                        Toast.makeText(getApplicationContext(),"saved item removed",Toast.LENGTH_SHORT).show();
                                        databsesaveditem.child(post_key).removeValue();
                                        viewHolder.saveditems.setImageResource(R.drawable.save);
                                        mProcesssave = false;

                                    }
                                    else
                                        {
                                            // save the item
                                            Toast.makeText(getApplicationContext(),"saved for later",Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().getReference().child("Blogs").child(post_key).addValueEventListener(new ValueEventListener() {
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


                                                        databsesaveditem.child(post_key).setValue(retrievesaving);
                                                        viewHolder.saveditems.setImageResource(R.drawable.saved);
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


                                                            databsesaveditem.child(post_key).setValue(retrievesaving);
                                                            viewHolder.saveditems.setImageResource(R.drawable.saved);

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
                });
                //
            }
        };

        blogrecycle.setAdapter(firebaseRecyclerAdapter);

    }



    public static class BloggettingHolder extends RecyclerView.ViewHolder
    {
        View myview;
        ImageView like,saveditems;

        public BloggettingHolder(View itemView) {
            super(itemView);
            myview = itemView;

            like = (ImageView)myview.findViewById(R.id.like_id);
            saveditems = (ImageView)myview.findViewById(R.id.saved_id);


        }


        public void setHeading(String heading){
            TextView mess = (TextView)myview.findViewById(R.id.text_id);
            mess.setText(heading);
        }
        public void setTime(String time){
            TextView tim = (TextView)myview.findViewById(R.id.timeretrieve_id);
            tim.setText(time);
        }
        public void setDate(String date){
            TextView dat = (TextView)myview.findViewById(R.id.dateretrieve_id);
            dat.setText(date);
        }

        public void setPhoto(String photo){
            ImageView imgv = (ImageView)myview.findViewById(R.id.photoretrieve_id);
            Picasso.get().load(photo).into(imgv);
        }

        public void setCount(String count) {
            TextView like = (TextView)myview.findViewById(R.id.likecount_id);
            like.setText(count);
        }

    }


    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /* private void updateCounter(final boolean increment) {
        mDatabaseLikeCount.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() != null) {
                    int value = mutableData.getValue(Integer.class);
                    if(increment) {
                        value++;
                    } else {
                        value--;
                    }
                    mutableData.setValue(value);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "likeTransaction:onComplete:" + databaseError);
            }
        });
    }
    */


}
