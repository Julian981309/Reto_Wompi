package co.e2e.wompi.config;

import io.restassured.RestAssured;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Env {

    public static final String BASE_URL = System.getProperty("WOMPI_BASE_URL","https://api-sandbox.co.uat.wompi.dev/v1");
    public static final String PUB_KEY = System.getProperty("WOMPI_PUBLIC_KEY", "pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7");
    public static final String PRV_KEY = System.getProperty("WOMPI_PRIVATE_KEY", "prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg");
    public static final String INTEGRITY = System.getProperty("WOMPI_INTEGRITY", "stagtest_integrity_nAIBuqayW70XpUqJS4qf4STYiISd89Fp");

    public static void configure() {

        RestAssured.baseURI = BASE_URL;
        RestAssured.useRelaxedHTTPSValidation();
    }

    public static String integrity(String reference, long amountIncents, String currency){
        String toHash = reference + amountIncents + currency + INTEGRITY;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(toHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating integrity hash", e);
        }
    }
}
