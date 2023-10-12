package com.camilo.TGSServer.model;

import java.io.Serializable;

public class ClientMessage implements Serializable  {
    private String encryptedData;
    private String tgsTicket;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getTgsTicket() {
        return tgsTicket;
    }

    public void setTgsTicket(String tgsTicket) {
        this.tgsTicket = tgsTicket;
    }
}
