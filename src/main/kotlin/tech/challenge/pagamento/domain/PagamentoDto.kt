package tech.challenge.pagamento.domain

data class PagamentoDto(
    val id: String?,
    val pedido: Long?,
    val status: PagamentoStatus?
)