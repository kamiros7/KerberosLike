package com.example.KerberosLike.model;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private int clientId;
    private String cryptMessage;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getCryptMessage() {
        return cryptMessage;
    }

    public void setCryptMessage(String cryptMessage) {
        this.cryptMessage = cryptMessage;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "clientId=" + clientId +
                ", cryptMessage='" + cryptMessage + '\'' +
                '}';
    }
}
