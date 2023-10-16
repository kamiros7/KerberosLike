package org.camilo.service.serviceProduct;

import com.google.gson.Gson;
import org.camilo.model.serviceProduct.ClientInfoMessage;
import org.camilo.utils.CryptUtils;

import javax.crypto.*;
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

    public String encryptedClientInfoMessage(ClientInfoMessage clientInfoMessage, String serviceSessionKey) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String encryptedClientMessageJson = GSON.toJson(clientInfoMessage);
        SecretKey key = CryptUtils.getKeyFromPassword(serviceSessionKey, "2");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(encryptedClientMessageJson.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }
}
