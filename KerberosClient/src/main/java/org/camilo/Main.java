package org.camilo;

import org.camilo.controller.KerberosController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        KerberosController kerberosController = new KerberosController();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (true) {
            System.out.println("Choose the step:");
            System.out.println("1 - Get TGS Ticket");
            System.out.println("2 - Get Service Ticket");
            System.out.println("3 - Use Service");
            System.out.println("4 - Use Service");

            int step = scanner.nextInt();
            switch (step) {
                case 1:
                    kerberosController.buildNewConstraints();
                    kerberosController.requestTgsTicket();
                    break;
                case 2:
                    kerberosController.validateAsResponse();
                    kerberosController.requestServiceTicket();
                    break;
                case 3:
                    kerberosController.validateTgsResponse();
                    kerberosController.requestService();
                    kerberosController.validateServiceResponse();
                    break;
                default:
                    exit = true;
                    break;
            }

            if(exit) {
                break;
            }
        }
    }
}