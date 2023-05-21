package com.example.here.models;

import com.google.gson.annotations.SerializedName;

public class Invitation {

    int invitation_id;
    int sender;
    int recipient;

    public Invitation() {}

    public Invitation(int invitation_id, int sender, int recipient) {
        this.invitation_id = invitation_id;
        this.sender = sender;
        this.recipient = recipient;
    }

    public int getInvitation_id() {
        return invitation_id;
    }

    public void setInvitation_id(int invitation_id) {
        this.invitation_id = invitation_id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    


}
