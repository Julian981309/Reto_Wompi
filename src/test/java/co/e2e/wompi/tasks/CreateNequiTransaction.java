package co.e2e.wompi.tasks;

import co.e2e.wompi.config.Env;
import co.e2e.wompi.models.TransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.given;

public class CreateNequiTransaction implements Task {
    private final long amountInCents;
    private final String phoneNumber;
    private final String reference;
    private final boolean invalidSignature;

    public CreateNequiTransaction(long amountInCents, String phoneNumber, String reference, boolean invalidSignature) {
        this.amountInCents = amountInCents;
        this.phoneNumber = phoneNumber;
        this.reference = reference;
        this.invalidSignature = invalidSignature;
    }

    public static CreateNequiTransaction withDetails(long amountInCents, String phoneNumber, String reference) {
        return new CreateNequiTransaction(amountInCents, phoneNumber, reference, false);
    }

    public static CreateNequiTransaction withInvalidSignature(long amountInCents, String phoneNumber, String reference) {
        return new CreateNequiTransaction(amountInCents, phoneNumber, reference, true);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String acceptanceToken = actor.recall("ACCEPTANCE_TOKEN");

        Map<String, Object> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "NEQUI");
        paymentMethod.put("phone_number", phoneNumber);

        String hash = Env.integrity(reference, amountInCents, "COP");

        TransactionRequest requestBody = new TransactionRequest(
                amountInCents,
                "COP",
                "qa@example.com",
                reference,
                hash,
                acceptanceToken,
                paymentMethod
        );

        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + Env.PRV_KEY)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", reference)
                .body(requestBody)
                .post("/transactions");

        int statusCode = response.getStatusCode();
        String txId = response.jsonPath().getString("data.id");
        String txStatus = response.jsonPath().getString("data.status");

        actor.remember("LAST_STATUS_CODE", statusCode);
        actor.remember("TX_ID", txId);
        actor.remember("TX_STATUS", txStatus);

        System.out.println("HTTP Status: " + statusCode);
        System.out.println("Transaction ID: " + txId);
        System.out.println("Transaction Status: " + txStatus);
    }
}
