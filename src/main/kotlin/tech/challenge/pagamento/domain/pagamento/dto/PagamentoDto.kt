package tech.challenge.pagamento.domain.pagamento.dto

import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

data class PagamentoDto(
    val id: String?,
    val pedido: Long?,
    val status: PagamentoStatus?
)