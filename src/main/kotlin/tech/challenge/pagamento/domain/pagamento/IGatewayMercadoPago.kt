package tech.challenge.pagamento.domain.pagamento

import java.math.BigDecimal

interface IGatewayMercadoPago {

    fun processarPagamento(pedidoId: Long, valor: BigDecimal)
}