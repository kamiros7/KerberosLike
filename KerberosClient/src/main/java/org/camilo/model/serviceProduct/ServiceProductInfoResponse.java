package org.camilo.model.serviceProduct;

import java.io.Serializable;

public class ServiceProductInfoResponse implements Serializable {
    private String response;
    private String randomNumber;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }
}
