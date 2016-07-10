package com.phobos.system.heavykey;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

// processes relevant incoming sms messages

public class SmsFilter extends BroadcastReceiver {

    DBHandler dbHandler;
    byte[] pad;
    LocalBroadcastManager manager;
    AppSettings appSettings;


    @Override

    public void onReceive(Context context, Intent intent) {
        dbHandler = new DBHandler(context, null, null, 1);
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");

                if (pdus.length < 1) return; // nothing in the message

                StringBuilder sb = new StringBuilder();
                String senderNumber = null;
                String sender;

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    if (senderNumber == null) senderNumber = message.getOriginatingAddress();
                    String text = message.getMessageBody();
                    if (text != null) sb.append(text);


                }
                System.out.println("SENDER: " + senderNumber);

              try {
                    senderNumber = senderNumber.replaceAll( "[^\\d]", "" );


              }
                catch(Exception e)
                {
                    System.out.println("invalid number");
                    return;
                }
                String unprocessedMessage = sb.toString();
                //very poor temporary code only works for American numbers :^( //fixed :^) now should work for international numbers
                String[] numberStringArray = dbHandler.getNumbers();
                String[] contactStringArray = dbHandler.getContacts();
                int senderIndex = contactLooseSearch(senderNumber, numberStringArray);
                if(senderIndex == -1)
                {
                    return;
                }
                sender = contactStringArray[senderIndex];

                System.out.println(sender);
                System.out.println(dbHandler.entryExists(DBHandler.TABLE_CONTACTS, DBHandler.COLUMN_NAME, sender));
                //determines whether or not message is important
                if (sb.toString().length() > 32 && sender != null) {
                    if (dbHandler.entryExists(DBHandler.TABLE_CONTACTS, DBHandler.COLUMN_NAME, sender)) {
                        pad = dbHandler.getReceivePad(sender);
                        String padHeader = EncryptionHandler.getHeader(pad);
                        String messageHeader = EncryptionHandler.getHeader(unprocessedMessage);
                        System.out.println(messageHeader);
                        System.out.println(padHeader);
                        if (messageHeader.equals(padHeader)) {

                            processMessage(sender, unprocessedMessage, context,dbHandler);

                        } else {

                            System.out.println("header does not match");
                            Context appContext = MainActivity.getContextOfApplication();
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);

                            if (sp.getBoolean("resync",true)) {
                                System.out.println("Attempting resynchronization");
                                int index = EncryptionHandler.findHeader(pad, EncryptionHandler.headerFromString(EncryptionHandler.getHeader(unprocessedMessage)));
                                System.out.println(index + "is the index");
                                if (index != -1) {
                                    byte[] syncedPad = EncryptionHandler.resync(pad, index);
                                    System.out.println(syncedPad.length + "is the remaining pad length");

                                    dbHandler.setReceivePad(syncedPad, sender);
                                    processMessage(sender, unprocessedMessage, context,dbHandler);
                                    System.out.println("resync successful");

                                } else {
                                    System.out.println("resync unsuccessfull");
                                }


                            }
                        }


                    } else {
                        System.out.println("number not recognized");
                    }
                }
            }
            dbHandler.close();
        }
    }



    private static byte[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        byte result[] = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Byte.parseByte(strings[i]);
        }
        return result;
    }

    public void processMessage( String sender, String unprocessedMessage, Context context, DBHandler dbHandler) {
        if (dbHandler.entryExists(DBHandler.TABLE_CONTACTS, DBHandler.COLUMN_NAME, sender)) {

           /* pad = dbHandler.getReceivePad(sender);
            String padHeader = EncryptionHandler.getHeader(pad);
            String messageHeader = EncryptionHandler.getHeader(unprocessedMessage);
            System.out.println(messageHeader);
            System.out.println(padHeader);
            if (messageHeader.equals(padHeader)) {*/

                System.out.println(unprocessedMessage + ":" + sender);

                //decryption
                pad = dbHandler.getReceivePad(sender);
                pad = EncryptionHandler.stripHeader(pad);
                dbHandler.setReceivePad(pad, sender);
                String message = EncryptionHandler.xorDecrypt(pad, unprocessedMessage.substring(32));


                dbHandler.addMessage(sender, "Incoming: " + message);
                dbHandler.setReceivePad(EncryptionHandler.stripPad(pad, message), sender);

                //sends refresh broadcast
                Intent sendIntent = new Intent("refresh");
                LocalBroadcastManager.getInstance(context).sendBroadcast(sendIntent);
                //sends system notification
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notification)
                                .setContentTitle("New Message!")
                                .setContentText("Message from " + sender);

                Intent resultIntent = new Intent(context, chooseContact.class);


                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(666, mBuilder.build());



        }
    }

    public static int contactLooseSearch(String input, String[] contactList)
    {
      //easy "contains explicit" search
        for(int i = 0; i < contactList.length; i ++)
        {
            if (input.contains(contactList[i]) || contactList[i].contains(input))
            {
                return i;
            }
        }
      // contains "in order" search contactlist in input
        for(int i = 0; i < contactList.length; i ++)
        {
            int counter = 0;
            char nextChar = contactList[i].charAt(0);
           for(int c = 0; c < input.length(); c++)
           {
               if (contactList[i].charAt(i) == nextChar)
               {
                   counter+=1;
                   nextChar = contactList[i].charAt(counter);
               }
           }
            if (counter ==contactList[i].length()-1 )
            {
                return i;
            }
        }
        // contains "in order" search input in contactlist
        for(int i = 0; i < contactList.length; i ++)
        {
            int counter = 0;
            char nextChar = input.charAt(0);
            for(int c = 0; c < contactList[i].length(); c++)
            {
                if (input.charAt(i) == nextChar)
                {
                    counter+=1;
                    nextChar = input.charAt(counter);
                }
            }
            if (counter ==input.length()-1 )
            {
                return i;
            }
        }
        return -1;
    }

}

