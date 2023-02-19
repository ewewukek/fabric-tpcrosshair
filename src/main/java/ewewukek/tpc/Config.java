package ewewukek.tpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(TPCrosshairClientMod.class);

    public static Path path;

    public static boolean enableIn3rdPerson;
    public static final boolean ENABLE_IN_3RD_PERSON = true;

    public static boolean enableBowDrawIndicator;
    public static final boolean ENABLE_BOW_DRAW_INDICATOR = true;

    public static void setDefaults() {
        enableIn3rdPerson = ENABLE_IN_3RD_PERSON;
        enableBowDrawIndicator = ENABLE_BOW_DRAW_INDICATOR;
    }

    public static void load() {
        setDefaults();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int commentStart = line.indexOf('#');
                if (commentStart != -1) line = line.substring(0, commentStart);

                line.trim();
                if (line.length() == 0) continue;

                try (Scanner s = new Scanner(line)) {
                    s.useLocale(Locale.US);
                    s.useDelimiter("\\s*=\\s*");

                    if (!s.hasNext()) throw new IOException("expected field name");
                    String key = s.next().trim();

                    if (key.equals("version")) continue;

                    if (!s.hasNext()) throw new IOException("expected value");
                    String value = s.next().trim();

                    switch (key) {
                    case "enableIn3rdPerson":
                        enableIn3rdPerson = Boolean.parseBoolean(value);
                        break;
                    case "enableBowDrawIndicator":
                        enableBowDrawIndicator = Boolean.parseBoolean(value);
                        break;
                    default:
                        throw new IOException("unrecognized field: " + key);
                    }
                }
            }
        } catch (NoSuchFileException e) {
            save();

        } catch (IOException e) {
            logger.warn("Could not read configuration file: ", e);
        }
    }

    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("version = 1\n");
            writer.write("enableIn3rdPerson = " + enableIn3rdPerson + "\n");
            writer.write("enableBowDrawIndicator = " + enableBowDrawIndicator + "\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }
}
