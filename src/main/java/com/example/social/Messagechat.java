package com.example.social;

public class Messagechat {
    String message,receiver,sender,timetamp;
    boolean isseen;

    public Messagechat() {
    }

    public Messagechat(String message, String receiver, String sender, String timetamp, boolean isseen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timetamp = timetamp;
        this.isseen = isseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(String timetamp) {
        this.timetamp = timetamp;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
