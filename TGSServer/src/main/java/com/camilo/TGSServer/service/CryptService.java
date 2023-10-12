package com.camilo.TGSServer.service;

import com.camilo.TGSServer.model.*;
import com.camilo.TGSServer.utils.CryptUtils;
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

@Service
public class CryptService {
    @Value("${passwordTgs}")
    private String passwordTgs;

    @Value("${passwordServiceI}")
    private String passwordServiceI;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final Gson GSON = new Gson();

    public String decryptTgsTicket(String message) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        //In client and server, the salt is 2
        SecretKey key = CryptUtils.getKeyFromPassword(passwordTgs, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(message));
        return new String(plainText);
    }

    public TGSTicket buildTGSTicket(String tgsTicketJson) {
        return GSON.fromJson(tgsTicketJson, TGSTicket.class);
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

    public ClientInfoMessage buildClientInfo(String tgsTicketJson) {
        return GSON.fromJson(tgsTicketJson, ClientInfoMessage.class);
    }

    public TCSTicket buildTCSTicket(ClientInfoMessage clientInfoMessage) {
        TCSTicket tcsTicket = new TCSTicket();
        tcsTicket.setIdClient(clientInfoMessage.getIdClient());
        tcsTicket.setServiceTime(clientInfoMessage.getServiceTime());
        tcsTicket.setSessionKey(generateRandomString(25));
        return tcsTicket;
    }

    public String encryptTCSTicket(TCSTicket tcsTicket) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        String encryptedClientASMessageJson = GSON.toJson(tcsTicket);
        SecretKey key = CryptUtils.getKeyFromPassword(passwordServiceI,"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientASMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public ClientInfoResponse buildClientInfoResponse(ClientInfoMessage clientInfoMessage, String sessionKey) {
        ClientInfoResponse clientInfoResponse = new ClientInfoResponse();
        clientInfoResponse.setServiceTime(clientInfoMessage.getServiceTime());
        clientInfoResponse.setRandomNumber(clientInfoMessage.getRandomNumber());
        clientInfoResponse.setSessionKey(sessionKey);
        return clientInfoResponse;
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

    public String buildClientResponseJson(String encryptedClientResponse, String encryptedTcsTicket) {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setEncryptedClientResponse(encryptedClientResponse);
        clientResponse.setEncryptedTcsTicket(encryptedTcsTicket);
        return GSON.toJson(clientResponse);
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
