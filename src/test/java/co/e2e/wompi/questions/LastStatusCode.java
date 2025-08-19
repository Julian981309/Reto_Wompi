package co.e2e.wompi.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class LastStatusCode implements Question<Integer>{
    public static LastStatusCode value() {
        return new LastStatusCode();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        Integer code = actor.recall("LAST_STATUS_CODE");

        if (code == null) {
            // evitaNullPointer si nunca se setea
            return -1;
        }
        return code;
    }

}
