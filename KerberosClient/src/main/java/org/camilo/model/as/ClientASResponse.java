package org.camilo.model.as;

import java.io.Serializable;

public class ClientASResponse implements Serializable {
    private String clientInfo;
    private String tgsTicket;

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getTgsTicket() {
        return tgsTicket;
    }

    public void setTgsTicket(String tgsTicket) {
        this.tgsTicket = tgsTicket;
    }

    @Override
    public String toString() {
        return "ClientASMessageResponse{" +
                "clientInfo='" + clientInfo + '\'' +
                ", tgsTicket='" + tgsTicket + '\'' +
                '}';
    }
}
