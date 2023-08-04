//Warning
// Unauthorized use, reproduction, or distribution of this code, in whole or in part, without the explicit permission of the owner, is strictly prohibited and may result in severe legal consequences under the relevant IT Act and other applicable laws.
// To use this code, you must first obtain written permission from the owner. For inquiries regarding licensing, collaboration, or any other use of the code, please contact virendratarte22@gmail.com.
// Thank you for respecting the intellectual property rights of the owner.
package com.a.v.virendra.tarate.project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private long pressedTime;
    FirebaseAuth mAuth;
    private EditText edtUsername,edtEmail,edtMobileNumber,edtotp;
    private Button btnSubmit,btnotp;
    private TextView txtLoginInfo;
    private  Boolean isSigningUp = true,otpVarified = false;
    private String verificationId;
    //username layout
    private TextInputLayout usernameLayout,emailLayout,passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hiding action bar
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);

        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        btnSubmit = findViewById(R.id.btnSubmit);
        edtotp = findViewById(R.id.edtotp);
        txtLoginInfo = findViewById(R.id.textlogin);
        btnotp = findViewById(R.id.btnotp);
        //username layout
        usernameLayout = findViewById(R.id.Edittextlayout1);
        emailLayout = findViewById(R.id.Edittextlayout2);



        //checking that user is log in or not

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            finish();
        }

        //on button click
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty() ||  edtMobileNumber.getText().toString().isEmpty() || edtotp.getText().toString().isEmpty()){
                    if(isSigningUp && edtUsername.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter Valid Input", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //veriffying otp
                verifyotp(edtotp.getText().toString());
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
                    edtEmail.setVisibility(View.GONE);
                    emailLayout.setVisibility(View.GONE);
                    btnSubmit.setText("Log In");
                    txtLoginInfo.setText("Don't have an account? Sign UP");
                    edtotp.setEnabled(false);
                }else{
                    isSigningUp = true;
                    edtUsername.setVisibility(View.VISIBLE);
                    usernameLayout.setVisibility(View.VISIBLE);
                    edtEmail.setVisibility(View.VISIBLE);
                    emailLayout.setVisibility(View.VISIBLE);
                    btnSubmit.setText("Sign UP");
                    txtLoginInfo.setText("Already have an account? Log in");
                    edtotp.setEnabled(false);
                }

            }
        });


        //when click on Generate OTP
        btnotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edtMobileNumber.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter A Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    String number = edtMobileNumber.getText().toString();
                    sendVerificationCode(number);
                    btnotp.setText("OTP Sent");
                    btnotp.setEnabled(false);

                    int otpTime = 30000;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnotp.setText("Generate OTP");
                                btnotp.setEnabled(true);


                            }
                        },otpTime);
                    }

                }

            }
        });



    }


    //verify otp method
    private void verifyotp(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(isSigningUp){
                                FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(), edtEmail.getText().toString(), "",edtMobileNumber.getText().toString()));
                                //starting next activity after sign up
                                //Toast.makeText(MainActivity.this, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                                finish();
                                Toast.makeText(MainActivity.this, "Signed UP Successfully", Toast.LENGTH_SHORT).show();

                                otpVarified = true;
                                edtotp.setEnabled(true);
                            }else {

                                //starting next activity after sign up
                                //Toast.makeText(MainActivity.this, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                                finish();
                                Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();

                                otpVarified = true;
                                edtotp.setEnabled(true);
                            }


                        }
                    }
                });

    }

    //to generate and send otp

     PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();

            if(code !=null){
                verifyotp(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            //Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //Invalid request
                    Toast.makeText(MainActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                     //The SMS quota for the project has been exceeded
                    Toast.makeText(MainActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
                } /*else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    //reCAPTCHA verification attempted with null Activity
                }*/

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationId = s;
        }
    };


    //sending OTP
    private void sendVerificationCode(String phoneNumber) {
        edtotp.setEnabled(true);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(MainActivity.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

//when back button is Pressed


    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }


}
