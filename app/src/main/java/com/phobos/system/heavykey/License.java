package com.phobos.system.heavykey;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;


// license display
public class License extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        TextView textView = (TextView) findViewById(R.id.licensetext);
        textView.setText("Copyright 2016 John Balis\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n" );

    }

}
