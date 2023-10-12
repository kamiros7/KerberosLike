package org.camilo.model.tgs;

import java.io.Serializable;

public class TGSResponse implements Serializable {
    private String encryptedClientResponse;
    private String encryptedTcsTicket;

    public String getEncryptedClientResponse() {
        return encryptedClientResponse;
    }

    public void setEncryptedClientResponse(String encryptedClientResponse) {
        this.encryptedClientResponse = encryptedClientResponse;
    }

    public String getEncryptedTcsTicket() {
        return encryptedTcsTicket;
    }

    public void setEncryptedTcsTicket(String encryptedTcsTicket) {
        this.encryptedTcsTicket = encryptedTcsTicket;
    }

    @Override
    public String toString() {
        return "TGSResponse{" +
                "encryptedClientResponse='" + encryptedClientResponse + '\'' +
                ", encryptedTcsTicket='" + encryptedTcsTicket + '\'' +
                '}';
    }
}
