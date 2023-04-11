package com.a.v.virendra.tarate.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageActivity extends AppCompatActivity {

    private static final String EncryptionKEY = "VRT0JST0PRS0VVT1"; // key i.e. 128bit

    private RecyclerView recyclerView;
    private EditText edtMessageInput;
    private TextView txtChattingWith;
    private ProgressBar progressBar;
    private ImageView imgToolbar,imgSend;

    private ArrayList<Message> messages;

    private MessageAdapter messageAdapter;

    String usernameOfTheRoommate,emailOfRoommate,chatRoomId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //removing action bar
        try {
            this.getSupportActionBar().hide();
        }catch(Exception e){

        }

        //getting information from intenet from Friends Activity
        usernameOfTheRoommate = getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate = getIntent().getStringExtra("email_of_roommate");



        recyclerView = findViewById(R.id.messageRecycler);
        edtMessageInput = findViewById(R.id.editText);
        txtChattingWith = findViewById(R.id.chattingPerson);
        progressBar = findViewById(R.id.messageProgress);
        imgToolbar = findViewById(R.id.toolbar_image);
        imgSend = findViewById(R.id.sendMessageImage);



        //set roommate name and email
        txtChattingWith.setText(usernameOfTheRoommate);


        messages = new ArrayList<>();

        //sending messsage after clicking sen img
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AES Method To Encrypt the Message
                FirebaseDatabase.getInstance().getReference("messages/"+chatRoomId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),emailOfRoommate,AESencryption(edtMessageInput.getText().toString())));
                edtMessageInput.setText("");
            }
        });

        messageAdapter = new MessageAdapter(messages,getIntent().getStringExtra("my_img"),getIntent().getStringExtra("img_of_roommate"),MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("img_of_roommate")).placeholder(R.drawable.account_img).error(R.drawable.account_img).into(imgToolbar);

        setUpChatRoom();



    }

    //method for chat rooms
    private void setUpChatRoom(){

        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String myUsername = snapshot.getValue(User.class).getUsername();

                //comparing usernames alphabetically

                if(usernameOfTheRoommate.compareTo(myUsername) > 0){

                    //for xyz comapring with abc alphabitacally

                    chatRoomId = myUsername + usernameOfTheRoommate;


                } else if (usernameOfTheRoommate.compareTo(myUsername) == 0) {

                    //for abc comparing with abc
                    chatRoomId = myUsername + usernameOfTheRoommate;
                }else {
                    chatRoomId = usernameOfTheRoommate + myUsername;
                }

                attachMessageListener(chatRoomId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("messages/"+ chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //AES Encryption method

    private String AESencryption(String Encryptedstr){

        try {
            SecretKeySpec keySpec = new SecretKeySpec(EncryptionKEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedValue = cipher.doFinal(Encryptedstr.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(encryptedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Encryptedstr;

    }





}