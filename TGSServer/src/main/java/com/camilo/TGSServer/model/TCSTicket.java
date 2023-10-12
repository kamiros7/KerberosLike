package com.camilo.TGSServer.model;

import java.io.Serializable;

public class TCSTicket implements Serializable {
    private int idClient;
    private int serviceTime;
    private String sessionKey;

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public String toString() {
        return "TCSTicket{" +
                "idClient=" + idClient +
                ", serviceTime=" + serviceTime +
                ", sessionKey='" + sessionKey + '\'' +
                '}';
    }
}
