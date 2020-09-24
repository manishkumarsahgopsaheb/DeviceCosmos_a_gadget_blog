package com.example.devicecosmos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity {
TextView logipage;
EditText useremail , password , confirmpassword;
Button signupbtn;
FirebaseAuth mAuth;
DatabaseReference databaseReference;
ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        logipage = (TextView)findViewById(R.id.loginpage_id);
        useremail = (EditText)findViewById(R.id.emailregister_id);
        password = (EditText)findViewById(R.id.passwordregister_id);
        confirmpassword = (EditText)findViewById(R.id.confirmpassword_id);
        signupbtn = (Button)findViewById(R.id.signupbtn_id);

        loadingbar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        logipage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeruser();
            }
        });
    }

    private void registeruser()
    {

        final String email = useremail.getText().toString();
        final String pass = password.getText().toString();
        String confirm = confirmpassword.getText().toString();

        if (email.isEmpty()){
            useremail.setError("Email is required");
            useremail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            useremail.setError("please enter the valid email");
            useremail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            password.setError("password is required");
            password.requestFocus();
            return;

        }
        if (pass.length() < 6) {
            password.setError("Minimum length of password should be 6");
            password.requestFocus();
            return;
        }

        if (confirm.isEmpty()){
            confirmpassword.setError("Please Re-Type your password");
            confirmpassword.requestFocus();
            return;
        }

        if (confirm.length() < 6) {
            confirmpassword.setError("Minimum length of password should be 6");
            confirmpassword.requestFocus();
            return;
        }

        if (pass.equals(confirm))
        {
            loadingbar.setTitle("Wait");
            loadingbar.setMessage("Registering you...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        HashMap registeruser = new HashMap<>();
                        registeruser.put("email",email);
                        registeruser.put("password",pass);

                        String key = databaseReference.push().getKey();

                        databaseReference.child(key).updateChildren(registeruser).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(signup.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signup.this,home.class);
                                    startActivity(intent);
                                    finish();
                                }

                                else
                                    {
                                        Toast.makeText(signup.this,"Registering Error ",Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                            }
                        });
                    }
                    else
                        {
                         if (task.getException() instanceof FirebaseAuthUserCollisionException)
                             {
                                 Toast.makeText(getApplicationContext(), "this email is already in use", Toast.LENGTH_LONG).show();
                                 loadingbar.dismiss();
                             }
                             else
                                {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                 }
                    }

                }
            });

        }
        else
            {
               Toast.makeText(signup.this,"password did not match",Toast.LENGTH_SHORT).show();
             }
    }
}
