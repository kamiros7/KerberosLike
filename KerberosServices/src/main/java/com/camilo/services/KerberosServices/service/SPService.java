package com.camilo.services.KerberosServices.service;

import com.camilo.services.KerberosServices.model.ClientInfoMessage;
import com.camilo.services.KerberosServices.model.ClientInfoResponse;
import com.camilo.services.KerberosServices.model.ClientResponse;
import com.camilo.services.KerberosServices.model.TCSTicket;
import com.camilo.services.KerberosServices.utils.CryptUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Calendar;

@Service
public class SPService {
    @Value("${passwordServiceI}")
    private String passwordServiceI;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final Gson GSON = new Gson();

    public String decryptTgsTicket(String message) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        //In client and server, the salt is 2
        SecretKey key = CryptUtils.getKeyFromPassword(passwordServiceI, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(message));
        return new String(plainText);
    }

    public TCSTicket buildTCSTicket(String tcsTicket) {
        return GSON.fromJson(tcsTicket, TCSTicket.class);
    }

    public boolean validateTcsTicket(TCSTicket tcsTicket) {
        long timeDifference = (Calendar.getInstance().getTimeInMillis() - tcsTicket.getCreatedDate().getTimeInMillis()) / (60 * 1000);
        return (timeDifference < 1);
    }

    public String decryptClientInfo(String message, String sessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        //In client and server, the salt is 2
        SecretKey key = CryptUtils.getKeyFromPassword(sessionKey, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(message));
        return new String(plainText);
    }

    public ClientInfoMessage buildClientInfoMessage(String jsonClientInfoMessage) {
        return GSON.fromJson(jsonClientInfoMessage, ClientInfoMessage.class);
    }

    public boolean validateService(int serviceId) {
        //is considered that serviceId with value 3 or more doesn't exist in my system
        return (serviceId < 3);
    }

    public ClientInfoResponse buildClientInfoResponse(boolean validService, Long randomNumber) {
        ClientInfoResponse clientInfoResponse = new ClientInfoResponse();
        clientInfoResponse.setResponse((validService) ? "OK" : "ERROR");
        clientInfoResponse.setRandomNumber(randomNumber);
        return  clientInfoResponse;
    }

    public String encryptClientInfoResponse(ClientInfoResponse clientInfoResponse, String sessionKey)  throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        String encryptedClientASMessageJson = GSON.toJson(clientInfoResponse);
        SecretKey key = CryptUtils.getKeyFromPassword(sessionKey,"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientASMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String buildJsonResponse(String encryptedData) {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setEncryptedData(encryptedData);
        return GSON.toJson(clientResponse);
    }
}
