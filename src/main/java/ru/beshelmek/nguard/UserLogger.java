package ru.beshelmek.nguard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserLogger {

    public File dataFolder;

    public UserLogger(File file) {
        if((dataFolder = file).exists()) return;
        dataFolder.mkdirs();
    }

    public List<String> getLogs(String playerName) {
        File file = new File(dataFolder, String.format("%s.txt", playerName));
        if(file.exists() && file.isFile()) {
            List<String> stringList = new ArrayList<>();
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    StandardCharsets.UTF_8))) {
                String string;
                while((string = bufferedReader.readLine()) != null)
                    stringList.add(string);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            return stringList;
        }

        return new ArrayList<>();
    }

    public void addLog(String playerName, String messageReceiver, String string) {
        try {
            File file = new File(dataFolder, String.format("%s.txt", playerName));
            if(!file.exists()) file.createNewFile();

            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(String.format("%s | %s | %s", messageReceiver, string,
                    Calendar.getInstance().getTime().toGMTString()));
            bufferWriter.newLine();

            bufferWriter.close();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}