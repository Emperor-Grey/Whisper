package com.firstcode.chatapplication.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;

    public Chat(String sender, String receiver, String message,boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeen(boolean seen) {
        this.isSeen = seen;
    }

    public boolean isSeen() {
        return isSeen;
    }
}
