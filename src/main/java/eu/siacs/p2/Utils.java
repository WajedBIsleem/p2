package eu.siacs.p2;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Utils {

    public static String random(int length, SecureRandom random) {
        final byte[] bytes = new byte[3 * length];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String combineAndHash(String... parts) {
        return DigestUtils.sha1Hex(Arrays.stream(parts).collect(Collectors.joining("\00")));
    }

    public static void log(String log) {
        try {
            File myObj = new File("log.txt");
            if (!myObj.exists()) {
                System.out.println("file does not exists");
                myObj.createNewFile();
                System.out.println(myObj.getAbsolutePath());
            }
            if (myObj.exists()) {
                System.out.println("file exists");
                FileWriter myWriter = new FileWriter(myObj);
                myWriter.write("\n");
                myWriter.write(log);
                System.out.println("file path : " + myObj.getAbsolutePath());
                myWriter.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {

        }
    }
}
