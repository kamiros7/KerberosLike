package org.camilo.model.serviceProduct;

import java.io.Serializable;

public class ServiceProductInfoResponse implements Serializable {
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
}
