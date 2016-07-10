package com.phobos.system.heavykey;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
//allows user to choose from list of contacts or perform an action on a contact such as delete or generate key
public class chooseContact extends AppCompatActivity {
    DBHandler dbHandler;
    BroadcastReceiver refreshReceiver;
    IntentFilter refreshFilter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        dbHandler = new DBHandler(this, null, null, 1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contact);

        //register refreshReceiver
        refreshFilter = new IntentFilter("refresh");
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("refresh"))
                {
                    final ListView list = (ListView) findViewById(R.id.list);
                    registerForContextMenu(list);


                    String[] values = dbHandler.getContacts();


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, values);



                    list.setAdapter(adapter);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, refreshFilter);



        //closes existing notifications

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        nMgr.cancel(666);



        final ListView list = (ListView) findViewById(R.id.list);
        registerForContextMenu(list);


        String[] values = dbHandler.getContacts();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);



        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                //
                Intent intent = new Intent(view.getContext(), messengerScreen.class);
                intent.putExtra("integer", itemPosition);
                startActivity(intent);


            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.contactmenu, menu);
    }

    @Override
    // list handler for contact actions
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int cellIndex = info.position;
            int menuItemIndex = item.getItemId();


            if (menuItemIndex == R.id.delete) {
            final ListView list = (ListView) findViewById(R.id.list);
            String title = list.getAdapter().getItem(cellIndex).toString();
           dbHandler.deleteContact(title);
            finish();
            startActivity(getIntent());

            } else if (menuItemIndex == R.id.makepad) {
                final ListView list = (ListView) findViewById(R.id.list);
                String name = list.getAdapter().getItem(cellIndex).toString();
                Intent intent = new Intent(this, MakePad.class);
                intent.putExtra("name", name);
                startActivity(intent);
            } else if (menuItemIndex == R.id.getpad) {
                final ListView list = (ListView) findViewById(R.id.list);
                String number = list.getAdapter().getItem(cellIndex).toString();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "pad.jpg");
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    System.out.println(Arrays.toString(bytes) + "arrayOutput");
                } catch (FileNotFoundException e) {
                    System.out.println("not found");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("what even");
                    e.printStackTrace();

                }
                dbHandler.setReceivePad(EncryptionHandler.getPadSecondHalf(bytes), number);
                dbHandler.setSendPad(EncryptionHandler.getPadFirstHalf(bytes), number);
                System.out.println("pad accepted");
                boolean deleted = file.delete();
            }
            else if (menuItemIndex == R.id.deleteMessages)
            {
                final ListView list = (ListView) findViewById(R.id.list);
                String number = list.getAdapter().getItem(cellIndex).toString();
                dbHandler.clearMessages(number);
            }
        } catch (Exception e) {

        }

        return true;
    }

    public void addContact(View view) {
        Intent intent = new Intent(this, AddContact.class);
        startActivity(intent);



    }
    // this code is being retain for reference, it is not currently in use
    public void clearContacts(View view) {
        dbHandler.erase();
        finish();
        startActivity(getIntent());
    }

    public void settings(View view) {
        Intent intent = new Intent(this, AppSettings.class);
        startActivity(intent);
    }




    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver);

    }

}
