package com.example.devicecosmos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
TextView signouutbtn;
TextView saveditems;
CircleImageView profimge;
StorageReference storageReference;
DatabaseReference databaseReference;
FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    TextView username,accountemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        signouutbtn = (TextView) findViewById(R.id.signout_id);
        profimge = (CircleImageView) findViewById(R.id.profilephoto_id);
        saveditems = (TextView)findViewById(R.id.saveditems_id);
        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users_details");

        username = (TextView)findViewById(R.id.name_id);
        accountemail = (TextView)findViewById(R.id.youraccount_id);



        profimge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,enlargeprofile.class);
                startActivity(intent);
            }
        });

        saveditems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this,savedlater.class);
                startActivity(intent);
            }
        });

        // for google
        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signouutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    gotologinacitvity();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();

            // storing details in our database
            String name = account.getDisplayName();
            String email = account.getEmail();
            String idtoken = account.getIdToken();
            String imageurl = account.getPhotoUrl().toString();


            HashMap ouruser = new HashMap<>();
            ouruser.put("Name",name);
            ouruser.put("Email",email);
            ouruser.put("IdToken",idtoken);
            ouruser.put("image",imageurl);

            String key = mAuth.getCurrentUser().getUid();
            databaseReference.child(key).updateChildren(ouruser).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {

                    if (task.isSuccessful())
                    {
                        // do the thing whatever you want

                    }

                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(profile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }

                }
            });



            username.setText(account.getDisplayName());
            accountemail.setText(account.getEmail());


          //  userId.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(profimge);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
            }

        }else{
            gotologinacitvity();

        }
    }

    private void gotologinacitvity()
    {
       Intent intent=new Intent(this,login.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(intent);
       finish();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
