package com.phobos.system.heavykey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
//allows user to turn off auto-sync or look at the license and privacy policy
public class AppSettings extends Activity {
    private Context context;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        CheckBox chBox = (CheckBox)findViewById(R.id.checkBox);
        chBox.setChecked(loadBoolean("resync"));

        chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                             @Override
                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                 saveBoolean("resync", isChecked);

                                             }
                                         }
        );

    }
    public AppSettings() {

    }

    public void saveBoolean(String key, boolean value){
      try {
          Context appContext = MainActivity.getContextOfApplication();
          SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
          SharedPreferences.Editor editor = sp.edit();
          editor.putBoolean(key, value);
          editor.commit();
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }

    }
    public boolean loadBoolean(String key){
        try {

            Context appContext = MainActivity.getContextOfApplication();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
            System.out.println(sp.getBoolean(key, true) + "ha!");
            return sp.getBoolean(key, true);
        }
        catch (Exception e)
        {
            return true;
        }
        }


    public void openPrivacy(View view) {
        Intent intent = new Intent(this, privacyPolicy.class);
        startActivity(intent);
    }

    public void openLicense(View view) {
        Intent intent = new Intent(this, License.class);
        startActivity(intent);
    }
}
