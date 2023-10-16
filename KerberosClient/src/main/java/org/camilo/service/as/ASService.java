package org.camilo.service.as;

import com.google.gson.Gson;
import org.camilo.model.as.ClientASMessage;
import org.camilo.model.as.ClientASResponse;
import org.camilo.model.as.ClientInfoASResponse;
import org.camilo.model.as.ClientInfoASMessage;
import org.camilo.utils.AppConfigUtils;
import org.camilo.utils.CryptUtils;

import javax.crypto.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class ASService {
    private final static Gson GSON = new Gson();

    public ClientInfoASMessage buildClientInfoASMessage(int serviceId, int serviceTime , Long N1) {
        ClientInfoASMessage clientInfoASMessage = new ClientInfoASMessage();
        clientInfoASMessage.setServiceId(serviceId);
        clientInfoASMessage.setServiceTime(serviceTime);
        clientInfoASMessage.setRandomNumber(N1);
        return  clientInfoASMessage;
    }
    public String encryptedClientInfoASMessage(ClientInfoASMessage clientInfoASMessage) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        String encryptedClientASMessageJson = GSON.toJson(clientInfoASMessage);
        SecretKey key = CryptUtils.getKeyFromPassword(AppConfigUtils.getInstance().getPasswordClient(),"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientASMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String buildClientASMessage(int clientId, String encryptedMessageJson) {
        ClientASMessage clientASMessage = new ClientASMessage();
        clientASMessage.setClientId(clientId);
        clientASMessage.setCryptMessage(encryptedMessageJson);
        return GSON.toJson(clientASMessage);
    }

    public String sendClientMessage(String clientMessage) throws IOException {
        // URL to send the POST request to
        URL url = new URL("http://localhost:8181/as/ticket");

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

    public ClientASResponse buildClientASMessageResponse(String json) {
        return GSON.fromJson(json, ClientASResponse.class);
    }

    public boolean validateN1Number(Long localN1, ClientInfoASResponse clientInfoASResponse) {
        return clientInfoASResponse != null && localN1.equals(clientInfoASResponse.getRandomNumber());
    }

    public ClientInfoASResponse buildClientInfoASMessageResponse(ClientASResponse clientASResponse) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String decryptedMessage = decryptClientASMessageResponse(clientASResponse);
        return GSON.fromJson(decryptedMessage, ClientInfoASResponse.class);
    }

    private String decryptClientASMessageResponse(ClientASResponse clientASResponse) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKey key = CryptUtils.getKeyFromPassword(AppConfigUtils.getInstance().getPasswordClient(),"2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(clientASResponse.getClientInfo()));
        return new String(plainText);
    }
}
