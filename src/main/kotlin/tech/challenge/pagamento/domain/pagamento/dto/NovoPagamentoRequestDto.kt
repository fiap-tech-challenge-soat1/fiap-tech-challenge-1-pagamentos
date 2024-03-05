package tech.challenge.pagamento.domain.pagamento.dto

import java.math.BigDecimal

data class NovoPagamentoRequestDto(
    val pedidoId: Long,
    val valorTotal: BigDecimal
)