package co.e2e.wompi.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TransactionStatus implements Question<String> {
    public static TransactionStatus value(){
        return new TransactionStatus(); }
    @Override
    public String answeredBy(Actor actor) {
        String status = actor.recall("TX_STATUS");
        if (status == null){
            // Si no hay estado recordado, devolvemos un valor por defecto
            return "UNKNOWN";
        }
        return status;
    }
}
