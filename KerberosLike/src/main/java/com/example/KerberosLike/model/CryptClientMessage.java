package com.example.KerberosLike.model;

import java.io.Serializable;

public class CryptClientMessage implements Serializable {
    private int serviceId;
    private int serviceTime;
    private Long randomNumber;

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
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
        return "CryptClientMessage{" +
                "serviceId=" + serviceId +
                ", serviceTime=" + serviceTime +
                ", randomNumber=" + randomNumber +
                '}';
    }
}
