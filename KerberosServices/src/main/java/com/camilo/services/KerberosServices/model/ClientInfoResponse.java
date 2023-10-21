package com.camilo.services.KerberosServices.model;

import java.io.Serializable;

public class ClientInfoResponse implements Serializable {
    private String response;
    private Long randomNumber;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
                "response='" + response + '\'' +
                ", randomNumber=" + randomNumber +
                '}';
    }
}
