package com.phobos.system.heavykey;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


// privacy policy display
public class privacyPolicy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        TextView textView = (TextView)findViewById(R.id.privacytext);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(Html.fromHtml("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
                "<HTML>\n" +
                "<HEAD>\n" +
                "</HEAD>\n" +
                "<BODY LANG=\"en-US\" DIR=\"LTR\">\n" +
                "<DIV ID=\"ppHeader\" DIR=\"LTR\">\n" +
                "\t<P STYLE=\"margin-bottom: 0in\"><FONT FACE=\"verdana\"><FONT SIZE=5 STYLE=\"font-size: 21pt\">Privacy\n" +
                "\tPolicy</FONT></FONT></P>\n" +
                "</DIV>\n" +
                "<DIV ID=\"ppBody\" DIR=\"LTR\">\n" +
                "\t<P ALIGN=JUSTIFY STYLE=\"margin-bottom: 0in\"><FONT SIZE=2 STYLE=\"font-size: 11pt\">Information\n" +
                "\tCollection</FONT></P>\n" +
                "\t<P ALIGN=JUSTIFY STYLE=\"margin-bottom: 0in\"><FONT SIZE=2 STYLE=\"font-size: 11pt\">3rd\n" +
                "\tParty Disclosure</FONT></P>\n" +
                "\t<P ALIGN=JUSTIFY STYLE=\"margin-bottom: 0in\"><FONT SIZE=2 STYLE=\"font-size: 11pt\">3rd\n" +
                "\tParty Links</FONT></P>\n" +
                "\t<P ALIGN=JUSTIFY STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "\t</P>\n" +
                "</DIV>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><STRONG>When do we collect information?</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">We collect information from you when\n" +
                "you send Crash Logs.</P>\n" +
                "<P><A NAME=\"infoUs\"></A><BR><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><A NAME=\"trDi\"></A><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><STRONG>Third-party disclosure</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">We do not sell, trade, or otherwise\n" +
                "transfer to outside parties your personally identifiable information.</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><A NAME=\"trLi\"></A><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><STRONG>Third-party links</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">We do not include or offer third-party\n" +
                "products or services on our app.</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><STRONG>Contact Information</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><STRONG><SPAN STYLE=\"font-weight: normal\">PhobosSystems@gmail.com</SPAN></STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\"><BR>Last Edited on 2016-07-06</P>\n" +
                "</BODY>\n" +
                "</HTML>"));
    }

}
