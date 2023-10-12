package com.example.KerberosLike.model;

public class ClientResponse {
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
}
