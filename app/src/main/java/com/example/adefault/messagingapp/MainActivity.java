package com.example.adefault.messagingapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button b;
    TextView changer;
    EditText username;
    EditText password;
    String user;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null)
            {
                Intent i = new Intent(MainActivity.this,chatlist.class);
                startActivity(i);
            }
        }
        catch(Exception e)
        {}

        b = (Button) findViewById(R.id.button2);
        username = (EditText) findViewById(R.id.editText);


        password = (EditText) findViewById(R.id.editText2);


        changer = (TextView) findViewById(R.id.textView);
    }

    public void login(View view)
    {
        user = username.getText().toString();
        pass = password.getText().toString();

        if(!user.isEmpty() && !pass.isEmpty()) {
            user+="@gmail.com";
            if (b.getText().toString().equals("login")) {
                mAuth.signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                                            .child("token_id")
                                            .setValue(token);
                                    Intent i = new Intent(MainActivity.this,chatlist.class);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
            else
            {
                mAuth.createUserWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                            .child("username").setValue(user);
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                                            .child("token_id")
                                            .setValue(token);

                                    Intent i = new Intent(MainActivity.this,chatlist.class);
                                    startActivity(i);


                                } else {

                                    if(pass.length()<6)
                                    {
                                        Toast.makeText(MainActivity.this, "Password - atleast 6 characters", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Account already exists with same Username", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                // ...
                            }
                        });
            }
        }
        else
        {
            Toast.makeText(this, "Username and Password required", Toast.LENGTH_SHORT).show();
        }
    }

    public void change(View view)
    {
        if(b.getText().toString().equals("login"))
        {
            b.setText("signup");
            changer.setText(",or Login");
        }
        else
        {
            b.setText("login");
            changer.setText(",or SignUp");
        }
    }

}
