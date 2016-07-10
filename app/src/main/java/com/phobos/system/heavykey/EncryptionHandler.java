package com.phobos.system.heavykey;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;


//math and key generation functions

public class EncryptionHandler {
    // generates randomized pad


    public static byte[] insecRandPad(int byteNum) {
        byte[] pad = new byte[byteNum];
        new Random().nextBytes(pad);
        return pad;
    }

    public static byte[] sysSecRandPad(int byteNum) {
        SecureRandom sr = new SecureRandom();
        byte[] output = new byte[byteNum];
        sr.nextBytes(output);
        return output;
    }

    //splits byte array in half and returns first half
    public static byte[] getPadFirstHalf(byte[] pad) {
        if (pad.length % 2 == 0) {
            return Arrays.copyOf(pad, pad.length / 2);
        } else {
            byte[] output = new byte[(pad.length - 1) / 2];
            System.arraycopy(pad, 0, output, 0, (pad.length - 1) / 2);
            return output;
        }
    }

    //splits byte array in half and returns second half
    public static byte[] getPadSecondHalf(byte[] pad) {
        if (pad.length % 2 == 0) {
            byte[] output = new byte[(pad.length) / 2];
            System.arraycopy(pad, ((pad.length) / 2), output, 0, pad.length / 2);
            return output;
        } else {
            byte[] output = new byte[(pad.length - 1) / 2];
            System.arraycopy(pad, ((pad.length - 1) / 2), output, 0, (pad.length - 1) / 2);
            return output;
        }
    }


    // applies pad to message
    public static String xorEncrypt(byte[] pad, String message) {
        System.out.println("PAD:" + pad);
        try {
            // utf
            byte[] encodedMessage = message.getBytes("UTF-8");
            byte[] padFragment = Arrays.copyOf(pad, encodedMessage.length);
            byte[] encryptedMessage = new byte[encodedMessage.length];
            // xor
            int i = 0;
            for (byte b : encodedMessage) {
                encodedMessage[i] = (byte) (b ^ padFragment[i++]);
            }
            //base 64
            return Base64.encodeToString(encodedMessage, Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            return "encryption failed!";
        }

    }


    //removes used bytes from pad
    public static byte[] stripPad(byte[] pad, String message) {
        try {
            byte[] encodedMessage = message.getBytes("UTF-8");
            return Arrays.copyOfRange(pad, encodedMessage.length, pad.length);
        } catch (UnsupportedEncodingException e) {
            System.out.println("encoding failed");
            return pad;
        }
    }

    public static byte[] stripHeader(byte[] pad) {
        return Arrays.copyOfRange(pad, 4, pad.length);
    }

    public static byte[] resync(byte[] pad, int index) {
        byte[] output =  Arrays.copyOfRange(pad, index, pad.length);
        return output;
    }


    // decryption code
    public static String xorDecrypt(byte[] pad, String message) {
        System.out.println("PAD:" + pad);
        //base 64
        byte[] encryptedMessageArray = Base64.decode(message, Base64.DEFAULT);
        //xor
        byte[] padFragment = Arrays.copyOf(pad, encryptedMessageArray.length);
        byte[] decodedMessage = new byte[encryptedMessageArray.length];

        int i = 0;
        for (byte b : decodedMessage) {
            decodedMessage[i] = (byte) (encryptedMessageArray[i] ^ padFragment[i++]);
        }
        //utf
        try {
            return new String(decodedMessage, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "decryption failed";
        }
    }

    public static String getHeader(byte[] pad) {
        try {
            byte[] header = Arrays.copyOfRange(pad, 0, 4);
            StringBuilder sb = new StringBuilder(header.length * Byte.SIZE);
            for (int i = 0; i < Byte.SIZE * header.length; i++)
                sb.append((header[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
            return sb.toString();
        } catch (Exception e) {
            return "null";
        }


    }

    public static byte[] headerFromString(String header)
    {
        byte[] raw = new BigInteger(header,2).toByteArray();
        if (raw.length >4)
        {
            return Arrays.copyOfRange(raw, 1, raw.length);
        }
        else
        {
            return raw;
        }
    }


    public static String getHeader(String message) {
        // System.out.println(message.substring(0,32));
        return message.substring(0, 32);
    }

    public static int getEncodedByteLength(String message) {
        try {
            byte[] encodedMessage = message.getBytes("UTF-8");
            // the 4 additional bytes are for the header.
            return encodedMessage.length + 4;
        } catch (UnsupportedEncodingException e) {
            System.out.println("encoding failed");
            return 0;
        }
    }
    // finds header anywhere it resides in pad for resynchronization.

    public static int findHeader(byte[] pad, byte[] header) {
        System.out.println(Arrays.toString(header));
        int headerIndex = 0;
        int foundIndex =0;
        for (int i = 0; i < pad.length; i++) {
            if (pad[i] == header[headerIndex]) {

                if (headerIndex == 0)
                {
                    foundIndex = i;
                }
                if (headerIndex == header.length-1) {
                    System.out.println("found index" + foundIndex);
                    return foundIndex;
                }
                headerIndex += 1;
            }
            else
            {
                headerIndex = 0;
            }
        }
        return -1;
    }


}
