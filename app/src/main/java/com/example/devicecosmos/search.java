package com.example.devicecosmos;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.inputmethod.EditorInfo.*;

public class search extends AppCompatActivity {
    Spinner filter;
    RecyclerView tagrecycle;
    private DatabaseReference mydatabase,mDatabaseLikeCount,databasechildcount,databsesaveditem;
    Boolean mProcessLike = false;
    Boolean mProcesssave = false;
    FirebaseAuth mAuth;
    String Uid;
    EditText searchedit;
    ImageView cancel,back;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        filter = (Spinner)findViewById(R.id.spinnerdoctortype_id);

        tagrecycle = (RecyclerView)findViewById(R.id.filtertag_id);
        tagrecycle.setHasFixedSize(true);
        tagrecycle.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar)findViewById(R.id.progress);
       searchedit = (EditText)findViewById(R.id.typeforsearch_id);
       cancel = (ImageView)findViewById(R.id.clear_id);
       back = (ImageView)findViewById(R.id.back_id);

        //searchedit.setOnEditorActionListener();

        mAuth =FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();

        searchedit.setOnEditorActionListener(editorListener);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedit.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(search.this,home.class);
                startActivity(intent);
                finish();
            }
        });

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id)
            {
                //String selectedItem = parent.getItemAtPosition(position).toString();

                String taggetting = filter.getSelectedItem().toString();
                Query query = FirebaseDatabase.getInstance().getReference().child("Blogs").orderByChild("tag").equalTo(taggetting);

                //
                FirebaseRecyclerAdapter<tagreference,tagHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<tagreference, tagHolder>
                        (tagreference.class,R.layout.tagrecycle,tagHolder.class,query)
                {
                    @Override
                    protected void populateViewHolder(tagHolder viewHolder, tagreference model, int position) {
                        viewHolder.setHeading(model.getHeading());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setCount(model.getCount());
                        viewHolder.setPhoto(model.getPhoto());
                        viewHolder.setTag(model.getTag());
                        final String userid = getRef(position).getKey();

                        ImageView likes = (ImageView)viewHolder.itemView.findViewById(R.id.likeimage_id);
                        final ImageView savekaro = (ImageView)viewHolder.itemView.findViewById(R.id.savedlater_id);

                        FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(userid)))
                                {
                                    savekaro.setImageResource(R.drawable.saved);
                                }
                                else
                                {
                                    savekaro.setImageResource(R.drawable.save);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(),blogdetails.class);
                                intent.putExtra("fetchblog_id",userid);
                                startActivity(intent);
                            }
                        });

                        likes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDatabaseLikeCount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).child("likecount");
                                databasechildcount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).child("count");
                                mDatabaseLikeCount.keepSynced(true);
                                //Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_LONG).show();

                                mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (mProcessLike)
                                        { //int Count = 0;
                                            if ((dataSnapshot.exists())&& (dataSnapshot.hasChild(Uid))) {
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
                                                mProcessLike = false;
                                            } else {
                                                Log.i("D Diary", "User Liked");
                                                mDatabaseLikeCount.child(Uid).setValue(mAuth.getCurrentUser().getDisplayName());

                                                long likes = dataSnapshot.getChildrenCount() + 1;
                                                String likecount  = Long.toString(likes);

                                                databasechildcount.setValue(likecount);

                                                //databasechildcount.setValue(dataSnapshot.getChildrenCount() + 1).toString();
                                                //updateCounter(true);
                                                //Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                                                mProcessLike = false;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        savekaro.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                                            if (dataSnapshot.exists() && dataSnapshot.hasChild(userid))
                                            {
                                                // considered as already saved item
                                                Toast.makeText(getApplicationContext(),"saved item removed",Toast.LENGTH_SHORT).show();
                                                databsesaveditem.child(userid).removeValue();
                                                savekaro.setImageResource(R.drawable.save);
                                                mProcesssave = false;

                                            }
                                            else
                                            {
                                                // save the item
                                                Toast.makeText(getApplicationContext(),"saved for later",Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).addValueEventListener(new ValueEventListener() {
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


                                                            databsesaveditem.child(userid).setValue(retrievesaving);
                                                            savekaro.setImageResource(R.drawable.saved);
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


                                                            databsesaveditem.child(userid).setValue(retrievesaving);
                                                            savekaro.setImageResource(R.drawable.saved);
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
                    }
                };
                tagrecycle.setAdapter(firebaseRecyclerAdapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    searchingheading();
                    break;
            }
            return false;
        }
    };

    public static class tagHolder extends RecyclerView.ViewHolder
    {

        View mview;

        public tagHolder(View itemView) {
            super(itemView);
            mview =itemView;

        }
        public void setPhoto(String photo){
            ImageView img = (ImageView)mview.findViewById(R.id.uploadimage_id);
            Picasso.get().load(photo).into(img);
        }

        public void setHeading(String heading) {
            TextView head = (TextView)mview.findViewById(R.id.heading_id);
            head.setText(heading);
        }
        public void setTime(String time) {
            TextView tim = (TextView)mview.findViewById(R.id.time_id);
            tim.setText(time);
        }
        public void setDate(String date) {
            TextView dat = (TextView)mview.findViewById(R.id.date_id);
            dat.setText(date);
       }
        public void setCount(String count){
            TextView likes = (TextView)mview.findViewById(R.id.countlikes_id);
            likes.setText(count);
        }

        public void setTag(String tag){
            TextView tagging = (TextView)mview.findViewById(R.id.tagoption_id);
            tagging.setText(tag);
        }

    }

    private void searchingheading() {
        String type = searchedit.getText().toString();
        //Toast.makeText(getApplicationContext(),type,Toast.LENGTH_SHORT).show();
        Query query  = FirebaseDatabase.getInstance().getReference().child("Blogs").orderByChild("heading").startAt(type)
                .endAt(type + "\uf8ff");

        //Query query1 = FirebaseDatabase.getInstance().getReference().child("Blogs").orderByChild("heading").
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerAdapter<tagreference,tagHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<tagreference, tagHolder>
                (tagreference.class,R.layout.tagrecycle,tagHolder.class,query)
        {
            @Override
            protected void populateViewHolder(tagHolder viewHolder, tagreference model, int position) {
                viewHolder.setHeading(model.getHeading());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setCount(model.getCount());
                viewHolder.setPhoto(model.getPhoto());
                viewHolder.setTag(model.getTag());
                final String userid = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);

                ImageView likes = (ImageView)viewHolder.itemView.findViewById(R.id.likeimage_id);
                final ImageView savekaro = (ImageView)viewHolder.itemView.findViewById(R.id.savedlater_id);

                FirebaseDatabase.getInstance().getReference().child("SavedItem").child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(userid)))
                        {
                            savekaro.setImageResource(R.drawable.saved);
                        }
                        else
                        {
                            savekaro.setImageResource(R.drawable.save);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getApplicationContext(),blogdetails.class);
                        intent.putExtra("fetchblog_id",userid);
                        startActivity(intent);
                    }
                });

                likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseLikeCount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).child("likecount");
                        databasechildcount =  FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).child("count");
                        mDatabaseLikeCount.keepSynced(true);
                        //Toast.makeText(getApplicationContext(),post_key,Toast.LENGTH_LONG).show();

                        mDatabaseLikeCount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (mProcessLike)
                                { //int Count = 0;
                                    if ((dataSnapshot.exists())&& (dataSnapshot.hasChild(Uid))) {
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
                                        mProcessLike = false;
                                    } else {
                                        Log.i("D Diary", "User Liked");
                                        mDatabaseLikeCount.child(Uid).setValue(mAuth.getCurrentUser().getDisplayName());

                                        long likes = dataSnapshot.getChildrenCount() + 1;
                                        String likecount  = Long.toString(likes);

                                        databasechildcount.setValue(likecount);

                                        //databasechildcount.setValue(dataSnapshot.getChildrenCount() + 1).toString();
                                        //updateCounter(true);
                                        //Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                                        mProcessLike = false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                savekaro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                    if (dataSnapshot.exists() && dataSnapshot.hasChild(userid))
                                    {
                                        // considered as already saved item
                                        Toast.makeText(getApplicationContext(),"saved item removed",Toast.LENGTH_SHORT).show();
                                        databsesaveditem.child(userid).removeValue();
                                        savekaro.setImageResource(R.drawable.save);
                                        mProcesssave = false;

                                    }
                                    else
                                    {
                                        // save the item
                                        Toast.makeText(getApplicationContext(),"saved for later",Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference().child("Blogs").child(userid).addValueEventListener(new ValueEventListener() {
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


                                                    databsesaveditem.child(userid).setValue(retrievesaving);
                                                    savekaro.setImageResource(R.drawable.saved);
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


                                                    databsesaveditem.child(userid).setValue(retrievesaving);
                                                    savekaro.setImageResource(R.drawable.saved);
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

            }
        };
        tagrecycle.setAdapter(firebaseRecyclerAdapter);
    }
}
