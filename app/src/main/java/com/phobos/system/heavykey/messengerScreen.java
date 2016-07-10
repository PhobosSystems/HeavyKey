package com.phobos.system.heavykey;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

// contact communications screen
public class messengerScreen extends AppCompatActivity {
    DBHandler dbHandler;
    int index;
  public  Context thisClass = this;
    int sendPadRemaining = 0;
    int receivePadRemaining = 0;
    BroadcastReceiver refreshReceiver;
    IntentFilter refreshFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHandler = new DBHandler(this, null, null, 1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_screen);
        // Get ListView object from xml
        final ListView list = (ListView) findViewById(R.id.list);
        registerForContextMenu(list);

        // gets the index of the phone number
        Intent intent = getIntent();
        index = intent.getIntExtra("integer", 1);
        // displays remaining bytes
        String name = dbHandler.getName(index);
        System.out.println("name: " +name);
        byte[] sendPad = dbHandler.getSendPad(name);
        byte[] receivePad = dbHandler.getReceivePad(name);
        try {
            sendPadRemaining = sendPad.length;
            receivePadRemaining = receivePad.length;
        }
        catch(Exception e)
        {
            sendPadRemaining = 0;
            receivePadRemaining = 0;
        }
        TextView display = (TextView) findViewById(R.id.textBytes);
        display.setText("Remaining Secure Bytes: " + Integer.toString(sendPadRemaining)+ " OUT | " + Integer.toString(receivePadRemaining) + " IN");
        System.out.println(index);
        // puts messages into listview
        String[] values = dbHandler.getMessages((index));


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        list.setAdapter(adapter);

        //register refreshReceiver
        refreshFilter = new IntentFilter("refresh");


        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("refresh"))
                {
                    String[] data = dbHandler.getMessages(index);
                    ArrayAdapter<String> refreshAdapter = new ArrayAdapter<String>(messengerScreen.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, data);
                    list.setAdapter(refreshAdapter);

                   // finish();
                    //startActivity(getIntent());
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, refreshFilter);

    }

    public void sendMessage(View view) {

        //message processing code
        EditText textEntry = (EditText) findViewById(R.id.sendField);
        String name = dbHandler.getName(index);
        System.out.println(name);
        String message = textEntry.getText().toString();

        String encryptedMessage = "unininstatiated";





            //send message code
            if (!message.equals("") && sendPadRemaining >= EncryptionHandler.getEncodedByteLength(message)) {
                try {
                    byte[] pad = dbHandler.getSendPad(name);
                    encryptedMessage = EncryptionHandler.getHeader(pad);
                    dbHandler.setSendPad(EncryptionHandler.stripHeader(pad), name);
                    pad = dbHandler.getSendPad(name);
                    encryptedMessage = encryptedMessage +  EncryptionHandler.xorEncrypt(pad,message);
                    finish();

                   SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(dbHandler.getNumber(index), null, encryptedMessage, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                    dbHandler.addMessage(name, "Outgoing: " + message);
                    dbHandler.setSendPad(EncryptionHandler.stripPad(pad, message), name);
                    finish();
                    startActivity(getIntent());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


        }}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
       MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.messengermenu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int cellIndex = info.position;
        int menuItemIndex = item.getItemId();
        if (menuItemIndex== R.id.copy)
        {
            final ListView list = (ListView) findViewById(R.id.list);
            String title = list.getAdapter().getItem(cellIndex).toString();
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text",title);
            clipboard.setPrimaryClip(clip);
        }
        else if (menuItemIndex == R.id.delete)
        {
            System.out.println(cellIndex);
            Intent intent = getIntent();
            index = intent.getIntExtra("integer", 1);
            String name = dbHandler.getName(index);
            String[] values = dbHandler.getMessages((index));
            System.out.println(Arrays.toString(values));
            String[] updatedValues = new String[values.length-1];
            int del = cellIndex; //- 1;
            int j = 0;
            for(int i=0; i < values.length; i++)
            {
                if(i != del)
                    updatedValues[j++] = values[i];
            }
            System.out.println(Arrays.toString(updatedValues));
            dbHandler.wipeMessages(name);
            if(updatedValues.length>1) {
                for (int i = 0; i < updatedValues.length; i++) {
                    dbHandler.addMessage(name, updatedValues[i]);
                }
            }
            System.out.println("lol");
            finish();
            startActivity(getIntent());
        }
        return true;
    }
}