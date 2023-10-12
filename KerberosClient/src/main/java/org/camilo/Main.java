package org.camilo;

import org.camilo.controller.KerberosController;
import org.camilo.model.as.ClientASResponse;
import org.camilo.model.as.ClientInfoASMessage;
import org.camilo.model.as.ClientInfoASResponse;
import org.camilo.model.tgs.TGSResponse;
import org.camilo.service.as.ASService;
import org.camilo.service.tgs.TGSService;
import org.camilo.utils.AppConfigUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Getting the service and time to user the service
        /*Scanner scanner = new Scanner(System.in);
        System.out.print("Inform the service: ");
        int serviceId = scanner.nextInt();
        System.out.println("");
        System.out.print("Inform the time to use the service: ");
        int serviceTime = scanner.nextInt();
        scanner.close();

        //Communication with AS to obtain the access to TGS server
        ASService asService = new ASService();
        String ASJsonResponse = "";
        Long randomNumberN1 = new Random().nextLong();
        try {
            //Construct the encrypted part of client message
            ClientInfoASMessage clientInfoMessage = asService.buildClientInfoASMessage(serviceId, serviceTime , randomNumberN1);
            String encryptedJson = asService.encryptedClientInfoASMessage(clientInfoMessage);
            //Construct the client message
            String clientMessage = asService.buildClientASMessage(
                    Integer.parseInt(AppConfigUtils.getInstance().getClientId()),
                    encryptedJson
            );
            ASJsonResponse = asService.sendClientMessage(clientMessage);
            if(ASJsonResponse.isEmpty()) {
                System.out.println("Cannot send message to server");
                System.exit(0);
            }
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | IOException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        //Receiving the AS Ticket, validate the random number and mount the message to TGS
        ClientASResponse clientASResponse = asService.buildClientASMessageResponse(ASJsonResponse);
        ClientInfoASResponse clientInfoASResponse;
        try {
            clientInfoASResponse = asService.buildClientInfoASMessageResponse(clientASResponse);
            if(!asService.validateN1Number(randomNumberN1, clientInfoASResponse)) {
                System.out.println("Random number N1 are different");
                System.exit(0);
            }
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        //Communication with TGS to obtain the ticket to use the services
        TGSService tgsService = new TGSService();
        Long randomNumberN2 = new Random().nextLong();
        String jsonClientMessage = "";
        String tgsJsonResponse = "";
        if(clientInfoASResponse != null) {
            try {
                String encryptedClientMessage = tgsService.buildEncryptedClientMessage(clientInfoASResponse, serviceId, serviceTime, randomNumberN2);
                jsonClientMessage = tgsService.buildJsonClientMessage(encryptedClientMessage, clientASResponse.getTgsTicket());
                tgsJsonResponse = tgsService.sendClientMessage(jsonClientMessage);
                if(tgsJsonResponse.isEmpty()) {
                    System.out.println("Cannot send message to server");
                    System.exit(0);
                }
            } catch (InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException |
                     NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("tgsJsonResponse: " + tgsJsonResponse);
        TGSResponse tgsResponse = tgsService.buildTGSResponse(tgsJsonResponse);
        System.out.println(tgsResponse.toString()); */

        KerberosController kerberosController = new KerberosController();
        kerberosController.validateTgsResponse();
    }
}