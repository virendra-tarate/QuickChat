package com.a.v.virendra.tarate.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername,edtPassword,edtEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo;

    private  Boolean isSigningUp = true;

    //username layout
    private TextInputLayout usernameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hiding action bar
        getSupportActionBar().hide();

        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        btnSubmit = findViewById(R.id.btnSubmit);

        txtLoginInfo = findViewById(R.id.textlogin);

        //username layout
        usernameLayout = findViewById(R.id.Edittextlayout1);

        //checking that user is log in or not

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            finish();
        }

        //on button click
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
                    if(isSigningUp && edtUsername.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter Valid Input", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //checking signup or login

                if(isSigningUp){
                    handleSignup();
                }else{
                    handleLogin();
                }

            }
        });


        //checking user want to login or sign up
        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSigningUp){
                    isSigningUp = false;
                    edtUsername.setVisibility(View.GONE);
                    usernameLayout.setVisibility(View.GONE);
                    btnSubmit.setText("Log In");
                    txtLoginInfo.setText("Don't have an account? Sign UP");
                }else{
                    isSigningUp = true;
                    edtUsername.setVisibility(View.VISIBLE);
                    usernameLayout.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Sign UP");
                    txtLoginInfo.setText("Already have an account? Log in");
                }

            }
        });


    }

    private void handleSignup(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //upload Data to Reale time Database

                    FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(),edtEmail.getText().toString(),""));
                    //starting next activity after sign up

                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    finish();


                    Toast.makeText(MainActivity.this,"Signed UP Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //starting nex activity after login

                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));

                    Toast.makeText(MainActivity.this,"Logged In Successfully" , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}