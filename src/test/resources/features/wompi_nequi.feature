Feature: Pagos con nequi
  Background:
    Given Configuro el host y llaves
    And Obtengo un acceptance token valido

    @aprobadaDeclinada
    Scenario Outline: Transaccion nequi aprobada o declinada
      When creo una transaccion Nequi por <amount_in_cents> COP con telefono <telefono> y referencia <referencia>
      Then la transaccion llega a un estado final <estadoFinal> antes de agotar el tiempo de espera

      Examples:
        | amount_in_cents | telefono   | referencia        | estadoFinal |
        | 200000          | 3991111111 | ref-nequi-apr-007 | APPROVED    |
        | 200000          | 3992222222 | ref-nequi-dec-006 | DECLINED    |



    @alterna1
    Scenario: Falla por firma de integridad invalida
      When intento crear una transacción NEQUI con una firma invalida
      Then la API responde con código 422 o 400 indicando error de firma

    @alterna2
    Scenario: Falla por acceptance_token ausente
      Given que limpio el acceptance_token en memoria
      When intento crear una transaccion NEQUI sin acceptance_token
      Then la API responde con error por parametros invalidos

    @sanidad
    Scenario: Obtener acceptance_token desde /merchants/{PUBLIC_KEY}
      When consulto el endpoint de merchants con la llave pública
      Then recibo un acceptance_token no vacío