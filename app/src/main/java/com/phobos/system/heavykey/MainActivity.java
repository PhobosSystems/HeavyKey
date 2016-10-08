package com.phobos.system.heavykey;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
// home screen and central hub

public class MainActivity extends AppCompatActivity {
    public static Context contextOfApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication= getApplication();



        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,Manifest.permission.NFC},
                1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if(grantResults.length == 0)
                {
                    return;
                }
                for(int i = 0; i < grantResults.length;i++)
                {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,Manifest.permission.NFC},
                                1);
                        return;
                    }
                }

                    Toast.makeText(MainActivity.this, "All Permissions accepted! :^)", Toast.LENGTH_SHORT).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
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
