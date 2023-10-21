package com.camilo.services.KerberosServices.controller;

import com.camilo.services.KerberosServices.model.ClientInfoMessage;
import com.camilo.services.KerberosServices.model.ClientInfoResponse;
import com.camilo.services.KerberosServices.model.ClientMessage;
import com.camilo.services.KerberosServices.model.TCSTicket;
import com.camilo.services.KerberosServices.service.SPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO implementar token com tempo
@RestController
@RequestMapping("/sp")
public class SPController {
    @Autowired
    SPService spService;

    @PostMapping("/service")
    public ResponseEntity<?> getTgsToken(@RequestBody ClientMessage clientMessage) {
        try {
            String decryptTcsTicket = spService.decryptTgsTicket(clientMessage.getServiceTicket());
            TCSTicket tcsTicket = spService.buildTCSTicket(decryptTcsTicket);
            if(!spService.validateTcsTicket(tcsTicket)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body("Your ticket to use the TGS server not is acceptable");
            }
            String decryptClientInfo = spService.decryptClientInfo(clientMessage.getEncryptedData(), tcsTicket.getSessionKey());
            ClientInfoMessage clientInfoMessage = spService.buildClientInfoMessage(decryptClientInfo);

            boolean validService = spService.validateService(clientInfoMessage.getServiceId());

            ClientInfoResponse clientInfoResponse = spService.buildClientInfoResponse(validService, clientInfoMessage.getRandomNumber());
            System.out.println(clientInfoResponse);
            String encryptedClientInfoResponse = spService.encryptClientInfoResponse(clientInfoResponse, tcsTicket.getSessionKey());
            String clientResponseJson = spService.buildJsonResponse(encryptedClientInfoResponse);
            System.out.println("clientResponseJson: " + clientResponseJson);
            return ResponseEntity.ok(clientResponseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred: " + e.getMessage());
        }
    }
}
