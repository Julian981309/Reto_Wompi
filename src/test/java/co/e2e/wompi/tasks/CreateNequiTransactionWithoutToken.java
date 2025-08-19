package co.e2e.wompi.tasks;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import java.util.HashMap;
import java.util.Map;

public class CreateNequiTransactionWithoutToken implements Task {

    private final long amountInCents;
    private final String phoneNumber;
    private final String reference;

    public CreateNequiTransactionWithoutToken(long amountInCents, String phoneNumber, String reference) {
        this.amountInCents = amountInCents;
        this.phoneNumber = phoneNumber;
        this.reference = reference;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Construimos el body de la solicitud
        Map<String, Object> body = new HashMap<>();
        body.put("currency", "COP");
        body.put("reference", reference);
        body.put("amount_in_cents", amountInCents);
        body.put("customer_email", "qa@example.com");
        body.put("acceptance_token", null);

        Map<String, String> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "NEQUI");
        paymentMethod.put("phone_number", phoneNumber);

        body.put("payment_method", paymentMethod);

        // Hacemos la llamada POST con RestAssured
        Response response = RestAssured.given()
                .baseUri("https://api-sandbox.co.uat.wompi.dev/v1")
                .header("Authorization", "Bearer prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg")
                .header("Idempotency-Key", reference)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(body)
                .post("/transactions")
                .andReturn();

        // Guardamos el response y el status en el Actor
        actor.remember("LAST_RESPONSE", response);
        actor.remember("LAST_STATUS_CODE", response.getStatusCode());
    }

    public static CreateNequiTransactionWithoutToken withDetails(long amountInCents, String phoneNumber, String reference) {
        return new CreateNequiTransactionWithoutToken(amountInCents, phoneNumber, reference);
    }
}
