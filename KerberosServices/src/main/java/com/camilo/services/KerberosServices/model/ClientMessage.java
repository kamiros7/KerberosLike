package com.camilo.services.KerberosServices.model;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    private String encryptedData;
    private String serviceTicket;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getServiceTicket() {
        return serviceTicket;
    }

    public void setServiceTicket(String serviceTicket) {
        this.serviceTicket = serviceTicket;
    }
}
