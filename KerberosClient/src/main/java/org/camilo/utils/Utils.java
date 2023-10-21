package org.camilo.utils;

import java.io.*;

public class Utils {
    private ClassLoader classLoader = Utils.class.getClassLoader();

    public static void writeFile(String message, String path) {
        try {
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String readFile(String path) {
        String message = "";
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            message = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(message.isEmpty()) {
            System.out.println("TGS response file is empty!");
            throw new RuntimeException(new Exception("TGS response file is empty!"));
        }

        return message;
    }
}

/*
 InputStream inputStream = classLoader.getResourceAsStream(path);
            if(inputStream == null) {
                throw new IOException(path + " file not found!");
            }
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            //FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            message = stringBuilder.toString();
 */
