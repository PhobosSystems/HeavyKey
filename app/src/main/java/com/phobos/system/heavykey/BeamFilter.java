package com.phobos.system.heavykey;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;

 // currently not operational, this will be made functional in a later update

public class BeamFilter extends Activity {
    private File mParentPath;

    DBHandler dbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_pad);
        System.out.println("Beam Received");

        dbHandler = new DBHandler(this, null, null, 1);

    final ListView list = (ListView) findViewById(R.id.contactList);



    String[] values = dbHandler.getContacts();

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()

    {

        @Override
        public void onItemClick (AdapterView < ? > parent, View view,
        int position, long id){

        int itemPosition = position;
        System.out.println(position);



    }

    }

    );
}
     // artifact code from when this was not an activity. It is still unclear to me whether this code will be necesary so I am retaining it.
   /* @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Incoming Data");
        String action = intent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            System.out.println("if statement triggered");
            Uri beamUri = intent.getData();
            File file = new File(beamUri.toString());
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(Arrays.toString(bytes));
        }

    }*/
}
