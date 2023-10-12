package com.example.KerberosLike.controller;


import com.example.KerberosLike.model.ClientMessage;
import com.example.KerberosLike.model.CryptClientMessage;
import com.example.KerberosLike.service.CryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/as")
public class ASController {

    @Autowired
    CryptService cryptService;

    @PostMapping("/ticket")
    public ResponseEntity<?> getAsToken(@RequestBody ClientMessage clientMessage) {
        try {
            String decryptClientMessage = cryptService.decryptClientMessage(clientMessage.getCryptMessage());
            CryptClientMessage cryptClientMessage = cryptService.processDecryptClientMessage(decryptClientMessage);
            String clientResponse = cryptService.buildClientResponse(cryptClientMessage.getRandomNumber(), clientMessage.getClientId(), cryptClientMessage.getServiceTime());
            return ResponseEntity.ok(clientResponse);
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException |
                 InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred: " + e.getMessage());
        }
    }
}
