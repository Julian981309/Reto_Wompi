package co.e2e.wompi.tasks;

import co.e2e.wompi.models.TransactionRequest;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CreateNequiTransactionInvalidSignature implements Task {

    private long amountInCents;
    private String phoneNumber;
    private String reference;

    public CreateNequiTransactionInvalidSignature(long amountInCents, String phoneNumber, String reference) {
        this.amountInCents = amountInCents;
        this.phoneNumber = phoneNumber;
        this.reference = reference;
    }

    public static CreateNequiTransactionInvalidSignature withData(long amountInCents, String phoneNumber, String reference) {
        return Tasks.instrumented(CreateNequiTransactionInvalidSignature.class, amountInCents, phoneNumber, reference);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String acceptanceToken = actor.recall("acceptance_token");

        Map<String, Object> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "NEQUI");
        paymentMethod.put("phone_number", phoneNumber);

        TransactionRequest requestBody = new TransactionRequest(
                amountInCents,
                "COP",
                "qa@example.com",
                reference,
                "deadbeef",            // Simulamos una firma inválida
                acceptanceToken,
                paymentMethod
        );

        // Ejecutamos el POST de la transacción
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + actor.recall("private_key"))
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", reference)
                .body(requestBody)
                .post("/transactions");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("HTTP Status: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        // Guardamos la respuesta para posteriores validaciones
        actor.remember("last_response", response);
        // Guardamos el código HTTP
        actor.remember("last_status_code", response.getStatusCode());

        // Guardamos el estado de la transacción, evitando NullPointer si falla
        if (response.getStatusCode() < 400 && response.jsonPath().get("data.status") != null) {
            actor.remember("transaction_status", response.jsonPath().getString("data.status"));
        } else {
            actor.remember("transaction_status", "UNKNOWN");
        }
    }
}
