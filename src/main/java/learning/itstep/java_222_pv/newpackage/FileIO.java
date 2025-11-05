
package learning.itstep.java_222_pv.newpackage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class FileIO {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss a");

    public void demo() {
        File currentDir = new File("./");

        if (currentDir.exists()) {
            System.out.println("Current directory exists:");
            System.out.println("Path: " + currentDir.getAbsolutePath());
        }

        if (currentDir.isFile()) {
            System.out.println("The object is a file.");
        }

        if (currentDir.isDirectory()) {
            System.out.println("The object is a directory. Listing contents:\n");
            File[] directoryContent = currentDir.listFiles();
            if (directoryContent != null) {
                for (File file : directoryContent) {
                    String lastModified = dateFormat.format(new Date(file.lastModified()));
                    String type = file.isDirectory() ? "<DIR>" : file.length() + " bytes";
                    System.out.printf("%s  %10s  %s%n", lastModified, type, file.getName());
                }
            }
        }

        File subDir = new File("./subdir");
        if (subDir.exists()) {
            subDir.delete();
            System.out.println("\nSubdirectory deleted.");
        } else {
            subDir.mkdir();
            System.out.println("\nSubdirectory created.");
        }

        System.out.println("\n===========================================");

        try (FileWriter writer = new FileWriter("test.txt")) {
            writer.write("Line 1\nLine 2\nLine 3.");
            writer.flush();
            System.out.println("File successfully written.");
        } catch (IOException ex) {
            System.out.println("Write error: " + ex.getMessage());
        }

        System.out.println("-------------------------------------------");

        try (FileReader reader = new FileReader("test.txt");
             Scanner scanner = new Scanner(reader)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            System.out.println("File successfully read.");
        } catch (IOException ex) {
            System.out.println("Read error: " + ex.getMessage());
        }

        System.out.println("-------------------------------------------");

        Map<String, String> config = new HashMap<>();
        try (InputStream input = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("db.ini"));
             Scanner scanner = new Scanner(input)) {

            while (scanner.hasNextLine()) {
                String raw = scanner.nextLine();
                String noComment = raw.split("[#;]", 2)[0].trim();
                if (noComment.isEmpty()) continue;

                int eq = noComment.indexOf('=');
                if (eq < 0) continue;

                String key = noComment.substring(0, eq).trim();
                String value = noComment.substring(eq + 1).trim();

                if (!key.isEmpty()) config.put(key, value);
            }

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.err.println("Resource 'db.ini' not found.");
        }

        System.out.println("\n[CONFIG]");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            System.out.printf("%s: %s%n", entry.getKey(), entry.getValue());
        }
    }
}
    

