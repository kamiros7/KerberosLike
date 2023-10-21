package org.camilo.service.serviceProduct;

import com.google.gson.Gson;
import org.camilo.model.serviceProduct.ClientInfoMessage;
import org.camilo.model.serviceProduct.ClientMessage;
import org.camilo.model.serviceProduct.ServiceProductInfoResponse;
import org.camilo.model.serviceProduct.ServiceProductResponse;
import org.camilo.model.tgs.ClientInfoTGSResponse;
import org.camilo.utils.CryptUtils;

import javax.crypto.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class SPService {
    private final static Gson GSON = new Gson();
    public ClientInfoMessage buildClientInfoMessage(int clientId, int serviceId, int serviceTime, Long randomNumber) {
        ClientInfoMessage clientInfoMessage = new ClientInfoMessage();
        clientInfoMessage.setClientId(clientId);
        clientInfoMessage.setServiceId(serviceId);
        clientInfoMessage.setServiceTime(serviceTime);
        clientInfoMessage.setRandomNumber(randomNumber);

        return clientInfoMessage;
    }

    public String buildEncryptedClientInfoMessage(ClientInfoMessage clientInfoMessage, String serviceSessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String encryptedClientMessageJson = GSON.toJson(clientInfoMessage);
        SecretKey key = CryptUtils.getKeyFromPassword(serviceSessionKey, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String buildJsonClientMessage(String encryptedClientInfo, String serviceTicket) {
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setEncryptedData(encryptedClientInfo);
        clientMessage.setServiceTicket(serviceTicket);
        return GSON.toJson(clientMessage);
    }

    public ServiceProductResponse buildServiceProductResponse(String json) {
        return GSON.fromJson(json, ServiceProductResponse.class);
    }

    public ServiceProductInfoResponse buildServiceProductInfoResponse(String encryptedData, String sessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String decryptedMessage = decryptClientSPResponse(encryptedData, sessionKey);
        return  GSON.fromJson(decryptedMessage, ServiceProductInfoResponse.class);
    }

    private String decryptClientSPResponse(String encryptedData, String sessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKey key = CryptUtils.getKeyFromPassword(sessionKey, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(encryptedData));
        return new String(plainText);
    }

    public String sendClientMessage(String clientMessage, int serviceId) throws IOException {
        // URL to send the POST request to
        String serviceUrl = "http://localhost:8383/sp/service?=" + serviceId;
        URL url = new URL(serviceUrl);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        // Enable input and output streams for the connection
        connection.setDoOutput(true);

        // Write the JSON data to the output stream
        try (OutputStream os = connection.getOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            osw.write(clientMessage);
            osw.flush();
        }

        // Get the HTTP response code
        int responseCode = connection.getResponseCode();
        String jsonResponse = "";

        if (responseCode == HttpURLConnection.HTTP_OK) {//Reading the response
            try (InputStream inputStream = connection.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {

                // Read the JSON response line by line
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                // Parse the JSON response
                jsonResponse = responseBuilder.toString();
                // Now you can work with the JSON data in 'jsonResponse'
            }
        }

        connection.disconnect();
        return jsonResponse;
    }

    public boolean validateN3Number(Long localN3, Long serverN3) {
        return localN3.equals(serverN3);
    }
}
