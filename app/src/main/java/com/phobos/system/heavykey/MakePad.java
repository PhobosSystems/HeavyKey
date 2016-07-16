package com.phobos.system.heavykey;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.util.Arrays;

// allows user to generate a key and send it to another device

public class MakePad extends AppCompatActivity {

    private String name;
    private int byteNum;
    DBHandler makePadDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_pad);
        makePadDb = new DBHandler(this, null, null, 1);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("name") != null) {
            name = bundle.getString("name");
            System.out.println(name);
        }

    }

    // adds the pad(time to make the pad managable)
    public void addPad(View view) {
        EditText entryField = (EditText) findViewById(R.id.enterBytes);
        if (isValidNumber(entryField.getText().toString())) {
            try {
                byteNum = Integer.parseInt(entryField.getText().toString());
                byteNum = byteNum * 2;
            } catch (NumberFormatException e) {
            byteNum = 666;
            }
            if (byteNum > 1000000) byteNum = 1000000;
            byte[] pad = null;
            // figures out which kind op pad generation we are going to be doing.
            if (view.getId() == R.id.insecRand) {
                pad = EncryptionHandler.insecRandPad(byteNum);
            }
            else if (view.getId() == R.id.secRandom)
            {
                pad = EncryptionHandler.sysSecRandPad(byteNum);
            }
            else
            {
                return;
            }
         //   System.out.println(Arrays.toString(pad));
            System.out.println(Arrays.toString(EncryptionHandler.getPadFirstHalf(pad)) + Arrays.toString(EncryptionHandler.getPadSecondHalf(pad)));
            // switch active code out here for local testing
        // makePadDb.setReceivePad(EncryptionHandler.getPadFirstHalf(pad), name);
        //  makePadDb.setSendPad(EncryptionHandler.getPadSecondHalf(pad), name);
           makePadDb.setReceivePad(pad, name);
           makePadDb.setSendPad(pad, name);
            String dbString = makePadDb.extractContactData(DBHandler.TABLE_CONTACTS);
            System.out.println(dbString);

            //sends refresh broadcast

            finish();
            Intent sendIntent = new Intent("refresh");
            LocalBroadcastManager.getInstance(this.getParent()).sendBroadcast(sendIntent);
            //saves file so it can be sent

            File file = new File(Environment.getExternalStorageDirectory(), "pad.jpg");
            boolean deleted = file.delete();

            SavePad savePad = new SavePad();
            savePad.setFileName("pad.jpg");
            savePad.execute(pad);




            //code to send pads over bluetooth
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image/jpg");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "pad.jpg")));

            startActivity(Intent.createChooser(i, "Send pads"));


            //diagnostic code to detect pad if present
      /*      File file = new File(Environment.getExternalStorageDirectory(), "pad.jpg");
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
                System.out.println("ioe");
                e.printStackTrace();
            }*/
        }

    }

    static boolean isValidNumber(String value) {
        // Loop over all characters in the String.
        // ... If isDigit is false, this method too returns false.
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
