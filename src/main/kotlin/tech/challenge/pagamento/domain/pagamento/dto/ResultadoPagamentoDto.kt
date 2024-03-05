package tech.challenge.pagamento.domain.pagamento.dto

import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

data class ResultadoPagamentoDto(
    val pedido: Long,
    val resultadoPagamento: PagamentoStatus
)