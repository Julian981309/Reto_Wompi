package co.e2e.wompi.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class TransactionRequest {

    @JsonProperty("amount_in_cents")
    public long amount_In_Cents;
    public String currency;
    @JsonProperty("customer_email")
    public String customer_email;
    public String reference;
    public String signature;
    @JsonProperty("acceptance_token")
    public String acceptance_token;
    @JsonProperty("payment_method")
    public Map<String, Object> payment_method;

    public TransactionRequest(long amount, String currency, String email, String reference,
                              String signature, String acceptanceToken, Map<String, Object> pm) {
        this.amount_In_Cents = amount;
        this.currency = currency;
        this.customer_email = email;
        this.reference = reference;
        this.signature = signature;
        this.acceptance_token = acceptanceToken;
        this.payment_method = pm;
    }

}
