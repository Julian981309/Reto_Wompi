package co.e2e.wompi.tasks;

import co.e2e.wompi.config.Env;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static net.serenitybdd.rest.SerenityRest.given;

public class GetTransactionStatus implements Task {
    private final String txId;

    public GetTransactionStatus(String txId) {
        this.txId = txId;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Env.configure();

        var resp = given()
                .log().all()
                .header("Authorization", "Bearer " + Env.PRV_KEY)
                .basePath("/transactions/" + txId)
                .get()
                .then()
                .log().all()
                .extract();

        actor.remember("TX_STATUS", resp.path("data.status"));
    }

    public static GetTransactionStatus byId(String txId) {
        return new GetTransactionStatus(txId);
    }
}
