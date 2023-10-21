package com.example.KerberosLike.service;

import com.example.KerberosLike.model.ClientResponse;
import com.example.KerberosLike.model.CryptClientMessage;
import com.example.KerberosLike.model.CryptClientInfoResponse;
import com.example.KerberosLike.model.TGSTicket;
import com.example.KerberosLike.utils.CryptUtils;
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
public class CryptService {
    @Value("${passwordClient}")
    private String passwordClient;

    @Value("${passwordTgs}")
    private String passwordTgs;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final Gson GSON = new Gson();

    public String decryptClientMessage(String message) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        //In client and server, the salt is 2
        SecretKey key = CryptUtils.getKeyFromPassword(passwordClient,"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(message));
        return new String(plainText);
    }

    public CryptClientMessage processDecryptClientMessage(String decryptClientMessage) {
        return GSON.fromJson(decryptClientMessage, CryptClientMessage.class);
    }

    private TGSTicket buildTGSTicket(int clientId, int serviceTime) {
        TGSTicket tgsTicket = new TGSTicket();
        tgsTicket.setClientId(clientId);
        tgsTicket.setServiceTime(serviceTime);
        tgsTicket.setSessionKey(generateRandomString(25));
        tgsTicket.setCreatedDate(Calendar.getInstance());
        return tgsTicket;
    }

    public String buildClientResponse(Long randomNumber, int clientId, int serviceTime) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        TGSTicket tgsTicket = buildTGSTicket(clientId,serviceTime);
        CryptClientInfoResponse cryptClientInfoResponse = new CryptClientInfoResponse();
        cryptClientInfoResponse.setRandomNumber(randomNumber);
        cryptClientInfoResponse.setSessionKey(tgsTicket.getSessionKey());

        String cryptClientResponseJson = GSON.toJson(cryptClientInfoResponse);

        //Encrypt some information with client password and the ticket to use the TGS with the TGS password
        //Encrypt information about the client
        byte[] messageBytes = cryptClientResponseJson.getBytes();

        SecretKey key = CryptUtils.getKeyFromPassword(passwordClient,"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(messageBytes);
        String encryptedClientInfo = Base64.getEncoder()
                .encodeToString(cipherText);

        //Encrypt ticket
        String tgsTicketJson = GSON.toJson(tgsTicket);
        messageBytes = tgsTicketJson.getBytes();

        key = CryptUtils.getKeyFromPassword(passwordTgs,"2");
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(messageBytes);
        String encryptedTgsTicket = Base64.getEncoder()
                .encodeToString(cipherText);

        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setClientInfo(encryptedClientInfo);
        clientResponse.setTgsTicket(encryptedTgsTicket);

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