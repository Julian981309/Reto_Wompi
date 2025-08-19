package co.e2e.wompi.stepdefinitions;

import co.e2e.wompi.config.Env;
import co.e2e.wompi.questions.LastStatusCode;
import co.e2e.wompi.tasks.*;
import co.e2e.wompi.questions.TransactionStatus;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.es.*;
import net.serenitybdd.screenplay.Actor;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WompiSteps {
    private final Actor qa = Actor.named("QA");

    @Given("Configuro el host y llaves")
    public void configuroElHostYLlaves() {/* se usa Env.configure() dentro de las tasks */
    }

    @And("Obtengo un acceptance token valido")
    public void obtengoUnAcceptanceTokenValido() {
        qa.attemptsTo(GetAcceptanceToken.fromMerchants());
    }

    @When("^creo una transaccion Nequi por (.*) COP con telefono (.*) y referencia (.*)$")
    public void creoUnaTransaccionNequiPorMontoCOPConTelefonoTelefonoYReferenciaReferencia(String monto, String telefono, String referencia) throws InterruptedException {

        long amountInCents = Long.parseLong(monto) * 100; // Convertir a centavos
        qa.attemptsTo(CreateNequiTransaction.withDetails(amountInCents, telefono, referencia));

        System.out.println("LAST_STATUS_CODE = " + qa.recall("LAST_STATUS_CODE"));
        System.out.println("TX_ID = " + qa.recall("TX_ID"));
        System.out.println("TX_STATUS = " + qa.recall("TX_STATUS"));

        qa.attemptsTo(PollUntilFinalStatus.withDefaults());

    }

    @Then("^la transaccion llega a un estado final (.*) antes de agotar el tiempo de espera$")
    public void laTransaccionLlegaAUnEstadoFinalEstadoFinalAntesDeAgotarElTiempoDeEspera(String esperado) {

        qa.should(
                seeThat("Estado final de la transacción",
                    TransactionStatus.value(),
                        is(esperado)
                )

        );

    }


    @When("intento crear una transacción NEQUI con una firma invalida")
    public void invalidSignature() {
        long amountInCents = 300000;
        String phoneNumber = "3991111111";
        String reference = "TXN-" + System.currentTimeMillis();

        qa.attemptsTo(
                GetAcceptanceToken.fromMerchants());
        qa.remember("private_key", Env.PRV_KEY);
        qa.attemptsTo(
                CreateNequiTransactionInvalidSignature.withData(amountInCents, phoneNumber, reference)
        );
    }

    @Then("la API responde con código 422 o 400 indicando error de firma")
    public void assertInvalidSignature() {
        int statusCode = qa.recall("last_status_code");
        qa.should(
                seeThat("HTTP status code",
                        actor -> statusCode,
                        anyOf(is(422), is(400))
                )
        );
    }

    @Given("que limpio el acceptance_token en memoria")
    public void clearAcceptance() {
        qa.forget("ACCEPTANCE_TOKEN"); }

    @When("intento crear una transaccion NEQUI sin acceptance_token")
    public void createWithoutAcceptance() {
        qa.attemptsTo(CreateNequiTransactionWithoutToken.withDetails(300000, "3991111111", "ref-sin-acc-001"));
    }

    @Then("la API responde con error por parametros invalidos")
    public void laAPIRespondeConErrorPorParametrosInvalidos() {
        qa.should(
                seeThat("el código HTTP",
                        LastStatusCode.value(),
                        is(422)
                )
        );

        qa.should(
                seeThat("el estado de la transacción",
                        TransactionStatus.value(),
                        containsString("UNKNOWN")
                )
        );
    }

    @When("consulto el endpoint de merchants con la llave pública")
    public void consultMerchants() { qa.attemptsTo(GetAcceptanceToken.fromMerchants()); }

    @Then("recibo un acceptance_token no vacío")
    public void assertAcceptance() {
        qa.should(
                seeThat ("el acceptance_token",
                        actor -> actor.recall("ACCEPTANCE_TOKEN"),
                        not(isEmptyOrNullString())
                )
        );
    }
}
