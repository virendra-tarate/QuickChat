// This Project is Created by Team Interstellars For Solving For India Hack-a-thon by Geeks for Geeks
// ©️ All Rights Reserved By Team Interstellars
package com.a.v.virendra.tarate.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AboutApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        //removing title bar
        try{

            getSupportActionBar().hide();

        }catch(Exception e){

        }




    }
}
