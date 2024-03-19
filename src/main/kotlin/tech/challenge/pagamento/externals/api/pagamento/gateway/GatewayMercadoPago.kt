package tech.challenge.pagamento.externals.api.pagamento.gateway

import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.IGatewayMercadoPago
import java.math.BigDecimal

private const val ID_PEDIDO_PARA_FALHAR = "ID_PEDIDO_PARA_FALHAR"

@Component
class GatewayMercadoPago: IGatewayMercadoPago {
    override fun processarPagamento(pedidoId: Long, valor: BigDecimal) {
        val idPedidoParaFalhar = kotlin.runCatching {
            return@runCatching System.getenv(ID_PEDIDO_PARA_FALHAR).toLong()
        }.getOrNull()

        if(idPedidoParaFalhar == pedidoId) {
            throw RuntimeException("Falha na solicitação de pagamento")
        }
    }
}