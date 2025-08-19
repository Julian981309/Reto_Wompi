package co.e2e.wompi.tasks;

import co.e2e.wompi.config.Env;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import static net.serenitybdd.rest.SerenityRest.given;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetAcceptanceToken implements Task {
    public String token;

    public static GetAcceptanceToken fromMerchants() {
        return instrumented(GetAcceptanceToken.class);
    }


    @Override
    public <T extends Actor> void performAs(T actor) {
        Env.configure();
        var res = given().log().all().basePath("/merchants/" + Env.PUB_KEY).get()
                .then().log().all().statusCode(200)
                .extract().path("data.presigned_acceptance.acceptance_token");

        token = String.valueOf(res);
        actor.remember("ACCEPTANCE_TOKEN", token);
    }
}


