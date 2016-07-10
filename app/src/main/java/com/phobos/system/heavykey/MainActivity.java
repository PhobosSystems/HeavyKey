package com.phobos.system.heavykey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
// home screen and central hub

public class MainActivity extends AppCompatActivity {
    public static Context contextOfApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication= getApplication();
    }


    public void addContact(View view) {
        Intent intent = new Intent(this, AddContact.class);
        startActivity(intent);
    }

    public void openSendMessage(View view) {
        Intent intent = new Intent(this, chooseContact.class);
        startActivity(intent);
    }

    public void openHelp(View view)
    {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
}
