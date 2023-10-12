package com.example.KerberosLike.model;

import java.io.Serializable;

public class CryptClientInfoResponse implements Serializable {
    private Long randomNumber;
    private String sessionKey;

    public Long getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(Long randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
