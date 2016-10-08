package com.phobos.system.heavykey;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

// add contact interface
public class AddContact extends AppCompatActivity {

    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        dbHandler = new DBHandler(this, null, null, 1);
    }


    public void addContact(View view) {
        EditText phone = (EditText) findViewById(R.id.phoneEntry);
        EditText nameField = (EditText) findViewById(R.id.edtName);
        String number = phone.getText().toString();
        String name = nameField.getText().toString();
        name = name.replaceAll("[^a-zA-Z]", " ");


       /* if (!PhoneNumberUtils.isWellFormedSmsAddress(number))
        {
            return;
        }
        number = number.replaceAll("\\D+","");*/

        if (number.equals(""))
        {
            return;
        }
        if (name.length() < 1 || dbHandler.entryExists(DBHandler.TABLE_CONTACTS,DBHandler.COLUMN_NAME,name))
        {
            nameField.setText(R.string.InvalidInput);
        }
        for (int i = 0; i < number.length(); i++) {
            if (/*number.length() != 10 ||*/ !Character.isDigit(number.charAt(i))) { // length limit disabled so this is now international number friendly! :^)
                phone.setText(R.string.InvalidInput);
                return;
            }

        }


        //makes a new contact
        Contact contact = new Contact(number, name);
        //adds contact to sql database
        dbHandler.addContact(contact);
        //prints the entire database
        printDatabase();
        //opens the makepad activity and tells in the phone number of the added contact

        Intent sendIntent = new Intent("refresh");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
       /* Intent intent = new Intent(this, MakePad.class);
        intent.putExtra("phoneNumber", number);
        startActivity(intent);
        */
        finish();


    }
    public void getPadFromFile(View view) {

            EditText phone = (EditText) findViewById(R.id.phoneEntry);
            String number = phone.getText().toString();

       /* if (!PhoneNumberUtils.isWellFormedSmsAddress(number))
        {
            return;
        }
        number = number.replaceAll("\\D+","");*/

            // checks input for validity -- needs to be fixed to accomodate non American phone numbers
            for (int i = 0; i < number.length(); i++) {
                if (number.length() != 10 || !Character.isDigit(number.charAt(i))) {
                    phone.setText(R.string.InvalidInput);
                    return;
                }

            }

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

    //local method for getting contact data (button)
    public void printDatabase(View view) {
        String dbString = dbHandler.extractContactData(DBHandler.TABLE_CONTACTS);
        System.out.println(dbString);


    }

    //print database for when it's not triggered by a button
    public void printDatabase() {
        String dbString = dbHandler.extractContactData(DBHandler.TABLE_CONTACTS);
        System.out.println(dbString);

    }

    // local method for clearing contact data (button)
    public void clearContacts(View view) {
        dbHandler.erase();

    }

}
