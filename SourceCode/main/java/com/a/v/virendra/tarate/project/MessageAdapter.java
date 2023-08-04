//Warning
// Unauthorized use, reproduction, or distribution of this code, in whole or in part, without the explicit permission of the owner, is strictly prohibited and may result in severe legal consequences under the relevant IT Act and other applicable laws.
// To use this code, you must first obtain written permission from the owner. For inquiries regarding licensing, collaboration, or any other use of the code, please contact virendratarte22@gmail.com.
// Thank you for respecting the intellectual property rights of the owner.
package com.a.v.virendra.tarate.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private ArrayList<Message> messages;
    private String senderImg,reciverImg,currentTime;
    private Context context;

    private static final String EncryptionKEY = "VRT0JST0PRS0VVT1"; // key i.e. 128bit

    public MessageAdapter(ArrayList<Message> messages, String senderImg, String reciverImg, Context context) {
        this.messages = messages;
        this.senderImg = senderImg;
        this.reciverImg = reciverImg;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_holder,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {



        holder.txtxMessage.setText(AESdescryption(messages.get(position).getContet()));

        ConstraintLayout constraintLayout = holder.ccli;

        if(messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString())){
            //sending Messages

            //obtain image using glide lib
            Glide.with(context).load(senderImg).error(R.drawable.account_img).placeholder(R.drawable.account_img).into(holder.profileImg);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profileCardviewImg,ConstraintSet.LEFT);
            constraintSet.clear(R.id.messageText,ConstraintSet.LEFT);
            constraintSet.clear(R.id.cTime,ConstraintSet.LEFT);
            constraintSet.connect(R.id.profileCardviewImg,ConstraintSet.RIGHT,R.id.constraintlayout2,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.messageText,ConstraintSet.RIGHT,R.id.profileCardviewImg,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.cTime,ConstraintSet.RIGHT,R.id.constraintlayout2,ConstraintSet.RIGHT,4);
            constraintSet.connect(R.id.cTime,ConstraintSet.TOP,R.id.messageText,ConstraintSet.BOTTOM,4);
            constraintSet.applyTo(constraintLayout);


        }else {
            //reciving messages

            //obatin image of reciver using glid lib
            Glide.with(context).load(reciverImg).error(R.drawable.account_img).placeholder(R.drawable.account_img).into(holder.profileImg);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profileCardviewImg,ConstraintSet.RIGHT);
            constraintSet.clear(R.id.messageText,ConstraintSet.RIGHT);
            constraintSet.clear(R.id.cTime,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.profileCardviewImg,ConstraintSet.LEFT,R.id.constraintlayout2,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.messageText,ConstraintSet.LEFT,R.id.profileCardviewImg,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.cTime,ConstraintSet.LEFT,R.id.constraintlayout2,ConstraintSet.LEFT,4);
            constraintSet.connect(R.id.cTime,ConstraintSet.TOP,R.id.messageText,ConstraintSet.BOTTOM,4);
            constraintSet.applyTo(constraintLayout);

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }




    class MessageHolder extends RecyclerView.ViewHolder{

        ConstraintLayout ccli;
        TextView txtxMessage,currentTimetxt;
        ImageView profileImg;



        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            ccli = itemView.findViewById(R.id.constraintlayout2);
            txtxMessage = itemView.findViewById(R.id.messageText);
            currentTimetxt = itemView.findViewById(R.id.cTime);
            profileImg = itemView.findViewById(R.id.smallProfileImage);




        }



    }


    //AES Decryption Method

    private String AESdescryption(String Descryptedstr){

        try {
            SecretKeySpec keySpec = new SecretKeySpec(EncryptionKEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedValue = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decodedValue = Base64.getDecoder().decode(Descryptedstr);
            }
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            return new String(decryptedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Descryptedstr;

    }





}
