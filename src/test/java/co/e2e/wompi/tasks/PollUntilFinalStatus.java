package co.e2e.wompi.tasks;

import co.e2e.wompi.config.Env;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import static net.serenitybdd.rest.SerenityRest.given;

public class PollUntilFinalStatus implements Task {
    private final int maxAttempts; private final long sleepMillis;
    public static PollUntilFinalStatus withDefaults() { return new PollUntilFinalStatus(10, 1500); }

    public PollUntilFinalStatus(int maxAttempts, long sleepMillis) {
        this.maxAttempts = maxAttempts; this.sleepMillis = sleepMillis;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Env.configure();
        String id = actor.recall("TX_ID");
        String status = actor.recall("TX_STATUS");

        // Si no hay ID o el estado es nulo o un error, no hacemos polling
        if (id == null || status == null || status.startsWith("ERROR"))
            return;

            int attempts = 0;

        while (attempts++ < maxAttempts &&
                !(status.equals("APPROVED") ||
                        status.equals("DECLINED") ||
                        status.equals("VOIDED"))) {
            try {Thread.sleep(sleepMillis);} catch (InterruptedException ignored) {}
            // 🔹 Manejo seguro del request
            try {
                status = given()
                        .log().all()
                        .basePath("/transactions/" + id)
                        .get()
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .path("data.status");
                actor.remember("TX_STATUS", status);
            } catch (Exception e) {
                // Si Wompi devuelve error (ej. 404 porque no existe la transacción)
                actor.remember("TX_STATUS", "ERROR_POLLING");
                break;
            }
        }
    }
}

