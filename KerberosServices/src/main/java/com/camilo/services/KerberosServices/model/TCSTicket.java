package com.camilo.services.KerberosServices.model;

import java.util.Calendar;

public class TCSTicket {
    private int idClient;
    private int serviceTime;
    private String sessionKey;
    private Calendar createdDate;

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

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
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
