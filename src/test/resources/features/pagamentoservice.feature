Feature: Validar serviço de pagamento

  Scenario: Cliente solicita pagamento
    Given o pedido do cliente não possui pagamento pendente ou já processado
    When o servico de pagamento recebe a solicitacao
    Then o pagamento e gerado

  Scenario: Cliente solicita consulta do status do pagamento do pedido
    Given o pedido do cliente possui um pagamento
    When o cliente chama o /pagamentos/pedido/{pedido}
    Then o cliente recebe o status code 200
    And o cliente recebe o pagamento vinculado ao pedido