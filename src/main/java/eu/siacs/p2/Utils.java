package eu.siacs.p2;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

public class Utils {

    public static String random(int length, SecureRandom random) {
        final byte[] bytes = new byte[3 * length];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String combineAndHash(String... parts) {
        return DigestUtils.sha1Hex(Arrays.stream(parts).collect(Collectors.joining("\00")));
    }

    public static void log(String log){
        try {
            Files.write(Paths.get("/etc/p2/log.txt"), log.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
        }
    }

    static void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {

        }
    }
}
