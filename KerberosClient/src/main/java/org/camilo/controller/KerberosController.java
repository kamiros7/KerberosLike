package org.camilo.controller;

import org.camilo.model.as.ClientASResponse;
import org.camilo.model.as.ClientInfoASMessage;
import org.camilo.model.as.ClientInfoASResponse;
import org.camilo.model.serviceProduct.ClientInfoMessage;
import org.camilo.model.serviceProduct.ServiceProductInfoResponse;
import org.camilo.model.serviceProduct.ServiceProductResponse;
import org.camilo.model.tgs.ClientInfoTGSResponse;
import org.camilo.model.tgs.TGSResponse;
import org.camilo.service.as.ASService;
import org.camilo.service.serviceProduct.SPService;
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
    private final SPService spService = new SPService();
    private final static String AS_RESPONSE_PATH = "src/main/resources/files/asResponse.txt";
    private final static String TGS_RESPONSE_PATH = "src/main/resources/files/tgsResponse.txt";
    private final static String SP_RESPONSE_PATH = "src/main/resources/files/spResponse.txt";
    private List<Object> constraints = new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);

    public KerberosController() {
    }
    public void buildNewConstraints() {

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

    }
    public void requestTgsTicket() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            throw new RuntimeException("Constraints is NULL!");
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
            throw new RuntimeException("Constraints is NULL!");
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
            throw new RuntimeException("Constraints is NULL!");
        }
        //Communication with TGS to obtain the ticket to use the services
        String jsonClientMessage = "";
        String tgsJsonResponse = "";

        Object[] clientInfo = validateAsResponse();
        ClientASResponse clientASResponse = (ClientASResponse) clientInfo[0];
        ClientInfoASResponse clientInfoASResponse = (ClientInfoASResponse) clientInfo[1];

        if(clientInfoASResponse != null && clientASResponse != null) {
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

    public Object[] validateTgsResponse() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            throw new RuntimeException("Constraints is NULL!");
        }

        String tgsJsonResponse = Utils.readFile(TGS_RESPONSE_PATH);
        TGSResponse tgsResponse = tgsService.buildTGSResponse(tgsJsonResponse);

        //The sessionKey was created in AS Server and sending to client. Was stored in .txt file
        //and with validateAsResponse is get this string, decrypted to get the session key again
        ClientInfoASResponse clientInfoASResponse = (ClientInfoASResponse) validateAsResponse()[1];
        if(clientInfoASResponse == null) {
            throw new RuntimeException("Without session key to decrypt the data!");
        }
        ClientInfoTGSResponse clientInfoTGSResponse;
        try {
            clientInfoTGSResponse = tgsService.buildClientInfoTGSResponse(tgsResponse.getEncryptedClientResponse(), clientInfoASResponse.getSessionKey());
            if(tgsService.validateN2Number((long) constraints.get(2), clientInfoTGSResponse.getRandomNumber())) {
                System.out.println("Random number N2 are different");
                System.exit(0);
            }
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return new Object[]{tgsResponse, clientInfoTGSResponse};
    }

    public void requestService() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            throw new RuntimeException("Constraints is NULL!");
        }

        Object[] clientInfo = validateTgsResponse();
        TGSResponse tgsResponse = (TGSResponse) clientInfo[0];
        ClientInfoTGSResponse clientInfoTGSResponse = (ClientInfoTGSResponse) clientInfo[1];
        ClientInfoMessage clientInfoMessage = spService.buildClientInfoMessage(Integer.parseInt(AppConfigUtils.getInstance().getClientId()), (int) constraints.get(0), (int) constraints.get(1), (Long) constraints.get(4));

        if(tgsResponse != null && clientInfoTGSResponse != null) {
             try {
                String encryptedClientInfoMessage = spService.buildEncryptedClientInfoMessage(clientInfoMessage, clientInfoTGSResponse.getSessionKey());
                String jsonClientMessage = spService.buildJsonClientMessage(encryptedClientInfoMessage, tgsResponse.getEncryptedTcsTicket());
                String spJsonResponse = spService.sendClientMessage(jsonClientMessage, clientInfoMessage.getServiceId());
                if(spJsonResponse.isEmpty()) {
                    System.out.println("Cannot send message to server");
                    System.exit(0);
                }
                 //Saving the TGS Response
                 Utils.writeFile(spJsonResponse, SP_RESPONSE_PATH);
            } catch (InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException |
                     NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void validateServiceResponse() {
        //To doesn't generate null pointer exception
        if(constraints.isEmpty()) {
            throw new RuntimeException("Constraints is NULL!");
        }

        String spJsonResponse = Utils.readFile(SP_RESPONSE_PATH);
        ServiceProductResponse serviceProductResponse = spService.buildServiceProductResponse(spJsonResponse);
        ClientInfoTGSResponse clientInfoTGSResponse = (ClientInfoTGSResponse) validateTgsResponse()[1];

        try {
            ServiceProductInfoResponse serviceProductInfoResponse = spService.buildServiceProductInfoResponse(serviceProductResponse.getEncryptedData(), clientInfoTGSResponse.getSessionKey());
            if(spService.validateN3Number((long) constraints.get(3), serviceProductInfoResponse.getRandomNumber())) {
                System.out.println("Random number N2 are different");
                System.exit(0);
            }
            System.out.println("FINAL RESPONSE: " + serviceProductInfoResponse.getResponse());
        } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
