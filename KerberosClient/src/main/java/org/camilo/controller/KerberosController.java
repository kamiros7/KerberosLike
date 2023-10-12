package org.camilo.controller;

import org.camilo.model.as.ClientASResponse;
import org.camilo.model.as.ClientInfoASMessage;
import org.camilo.model.as.ClientInfoASResponse;
import org.camilo.model.tgs.TGSResponse;
import org.camilo.service.as.ASService;
import org.camilo.service.tgs.TGSService;
import org.camilo.utils.AppConfigUtils;
import org.camilo.utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class KerberosController {
    private final ASService asService = new ASService();
    private final TGSService tgsService = new TGSService();
    private final static String AS_RESPONSE_PATH = "/resources/files/asResponse.txt";
    private final static String TGS_RESPONSE_PATH = "/resources/files/tgsResponse.txt";
    private List<Object> constraints = new ArrayList<>();

    public void buildNewConstraints() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Inform the service: ");
        int serviceId = scanner.nextInt();
        System.out.print("Inform the time to use the service: ");
        int serviceTime = scanner.nextInt();

        Long randomNumberN1 = new Random().nextLong();
        Long randomNumberN2 = new Random().nextLong();
        Long randomNumberN3 = new Random().nextLong();

        constraints.clear();
        constraints = new ArrayList<>();
        constraints.add(serviceId);
        constraints.add(serviceTime);
        constraints.add(randomNumberN1);
        constraints.add(randomNumberN2);
        constraints.add(randomNumberN3);

        scanner.close();
    }
    public void requestTgsTicket() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            return;
        }
        //Communication with AS to obtain the access to TGS server
        String ASJsonResponse = "";
        try {
            //Construct the encrypted part of client message
            ClientInfoASMessage clientInfoMessage = asService.buildClientInfoASMessage((int) constraints.get(0), (int) constraints.get(1) , (long) constraints.get(2));
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

        //Saving the AS Response
        Utils.writeFile(ASJsonResponse, AS_RESPONSE_PATH);
    }

    public Object[] validateAsResponse() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            return new Object[]{};
        }
        //Get the ticket and another information saved in file
        String asResponseJson = Utils.readFile(AS_RESPONSE_PATH);

        //Receiving the AS Ticket, validate the random number and mount the message to TGS
        ClientASResponse clientASResponse = asService.buildClientASMessageResponse(asResponseJson);
        ClientInfoASResponse clientInfoASResponse;
        try {
            clientInfoASResponse = asService.buildClientInfoASMessageResponse(clientASResponse);
            if(!asService.validateN1Number((long) constraints.get(2), clientInfoASResponse)) {
                System.out.println("Random number N1 are different");
                System.exit(0);
            }
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return new Object[]{clientASResponse, clientInfoASResponse};
    }

    public void requestServiceTicket() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            return;
        }
        //Communication with TGS to obtain the ticket to use the services
        String jsonClientMessage = "";
        String tgsJsonResponse = "";

        Object[] clientInfo = validateAsResponse();
        ClientASResponse clientASResponse = (ClientASResponse) clientInfo[0];
        ClientInfoASResponse clientInfoASResponse = (ClientInfoASResponse) clientInfo[1];

        if(clientInfoASResponse != null) {
            try {
                String encryptedClientMessage = tgsService.buildEncryptedClientMessage(clientInfoASResponse, (int) constraints.get(0), (int) constraints.get(1), (long) constraints.get(3));
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

        //Saving the TGS Response
        Utils.writeFile(tgsJsonResponse, TGS_RESPONSE_PATH);
    }

    public void validateTgsResponse() {
        //To doesn't generate null pointer exception
        /*if(constraints.isEmpty()) {
            return;
        }*/

        String tgsJsonResponse = Utils.readFile(TGS_RESPONSE_PATH);

        System.out.println("tgsJsonResponse: " + tgsJsonResponse);
        TGSResponse tgsResponse = tgsService.buildTGSResponse(tgsJsonResponse);
        System.out.println(tgsResponse.toString());
    }

    public void requestService() {

    }

    public void validateServiceResponse() {

    }
}
