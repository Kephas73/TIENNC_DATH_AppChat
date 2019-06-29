package com.example.kephas73.meera.Model;

public class Chats {
    private String reviver;
    private String sender;
    private String messg;

    public Chats(String reviver, String sender, String messg) {
        this.reviver = reviver;
        this.sender = sender;
        this.messg = messg;
    }

    public Chats() {
    }

    public String getReviver() {
        return reviver;
    }

    public void setReviver(String reviver) {
        this.reviver = reviver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessg() {
        return messg;
    }

    public void setMessg(String messg) {
        this.messg = messg;
    }
}
