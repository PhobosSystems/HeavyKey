package com.phobos.system.heavykey;

/**
 * Created by John on 11/12/2015.
 */
public class Contact {
    private int _id;
    private String _phoneNumber = "empty";
    private byte[] sendPad;
    private byte[] recievePad;
    private String messages = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private  String name = "";

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public byte[] getRecievePad() {
        return recievePad;
    }

    public void setRecievePad(byte[] recievePad) {
        this.recievePad = recievePad;
    }

    public byte[] getSendPad() {
        return sendPad;
    }

    public void setSendPad(byte[] sendPad) {
        this.sendPad = sendPad;
    }

    //constructors
    public Contact() {

    }

    public Contact(String phoneNumber, String name) {
        this._phoneNumber = phoneNumber;
        this.name = name;
    }
    // functions


    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_phoneNumber(String _phoneNumber) {
        this._phoneNumber = _phoneNumber;
    }

    public String get_phoneNumber() {
        return _phoneNumber;
    }

    public int get_id() {
        return _id;
    }
}
