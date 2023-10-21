package org.camilo.service.tgs;

import com.google.gson.Gson;
import org.camilo.model.as.ClientInfoASResponse;
import org.camilo.model.tgs.ClientInfoTGSResponse;
import org.camilo.model.tgs.ClientMessage;
import org.camilo.model.tgs.ClientInfoMessage;
import org.camilo.model.tgs.TGSResponse;
import org.camilo.utils.AppConfigUtils;
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

public class TGSService {
    private final static Gson GSON = new Gson();

    public String buildEncryptedClientMessage(ClientInfoASResponse response, int serviceId, int serviceTime, Long randomNumberN2) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        ClientInfoMessage clientInfoMessage = new ClientInfoMessage();
        clientInfoMessage.setIdClient(Integer.parseInt(AppConfigUtils.getInstance().getClientId()));
        clientInfoMessage.setIdService(serviceId);
        clientInfoMessage.setServiceTime(serviceTime);
        clientInfoMessage.setRandomNumber(randomNumberN2);

        String encryptedClientMessageJson = GSON.toJson(clientInfoMessage);
        SecretKey key = CryptUtils.getKeyFromPassword(response.getSessionKey(), "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String buildJsonClientMessage(String encryptedData, String tgsTicket) {
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setEncryptedData(encryptedData);
        clientMessage.setTgsTicket(tgsTicket);
        return GSON.toJson(clientMessage);
    }

    public String sendClientMessage(String clientMessage) throws IOException {
        // URL to send the POST request to
        URL url = new URL("http://localhost:8282/tgs/ticket");

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

    public TGSResponse buildTGSResponse(String tgsJsonResponse) {
        return GSON.fromJson(tgsJsonResponse, TGSResponse.class);
    }

    public ClientInfoTGSResponse buildClientInfoTGSResponse(String encryptedData, String sessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String decryptedMessage = decryptClientTGSResponse(encryptedData, sessionKey);
        return GSON.fromJson(decryptedMessage, ClientInfoTGSResponse.class);
    }

    private String decryptClientTGSResponse(String encryptedData, String sessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKey key = CryptUtils.getKeyFromPassword(sessionKey, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(encryptedData));
        return new String(plainText);
    }

    public boolean validateN2Number(Long localN2, Long serverN2) {
        return localN2.equals(serverN2);
    }
}