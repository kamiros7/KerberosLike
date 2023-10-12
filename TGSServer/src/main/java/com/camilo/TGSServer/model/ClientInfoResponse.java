package com.camilo.TGSServer.model;

import java.io.Serializable;

public class ClientInfoResponse implements Serializable {
    private String sessionKey;
    private int serviceTime;
    private Long randomNumber;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public Long getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(Long randomNumber) {
        this.randomNumber = randomNumber;
    }

    @Override
    public String toString() {
        return "ClientInfoResponse{" +
                "sessionKey='" + sessionKey + '\'' +
                ", serviceTime=" + serviceTime +
                ", randomNumber=" + randomNumber +
                '}';
    }
}
