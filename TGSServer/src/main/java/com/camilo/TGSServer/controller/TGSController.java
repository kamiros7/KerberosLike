package com.camilo.TGSServer.controller;

import com.camilo.TGSServer.model.*;
import com.camilo.TGSServer.service.CryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/tgs")
public class TGSController {
    @Autowired
    CryptService cryptService;

    @PostMapping("/ticket")
    public ResponseEntity<?> getTgsToken(@RequestBody ClientMessage clientMessage) {
        try {
            String decryptTgsTicket = cryptService.decryptTgsTicket(clientMessage.getTgsTicket());
            TGSTicket tgsTicket = cryptService.buildTGSTicket(decryptTgsTicket);
            String decryptClientInfo = cryptService.decryptClientInfo(clientMessage.getEncryptedData(), tgsTicket.getSessionKey());
            ClientInfoMessage clientInfoMessage = cryptService.buildClientInfo(decryptClientInfo);

            //build T_C_S
            TCSTicket tcsTicket = cryptService.buildTCSTicket(clientInfoMessage);
            System.out.println(tcsTicket.toString());
            //encrypt T_C_S
            String encryptTCSTicket = cryptService.encryptTCSTicket(tcsTicket);
            System.out.println("encryptTCSTicket: " + encryptTCSTicket);
            //Build ClientInfoResponse
            ClientInfoResponse clientInfoResponse = cryptService.buildClientInfoResponse(clientInfoMessage, tcsTicket.getSessionKey());
            System.out.println(clientInfoResponse.toString());
            //Encrypt ClientInfoResponse
            String encryptClientInfoResponse = cryptService.encryptClientInfoResponse(clientInfoResponse, tgsTicket.getSessionKey());
            System.out.println("encryptClientInfoResponse: " + encryptClientInfoResponse);
            //Build ClientResponse
            String clientResponseJson = cryptService.buildClientResponseJson(encryptClientInfoResponse, encryptTCSTicket);
            System.out.println("clientResponseJson: " + clientResponseJson);
            return ResponseEntity.ok(clientResponseJson);
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException |
                 InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred: " + e.getMessage());
        }
    }
}
