package tech.challenge.pagamento.externals.api.pagamento.gateway

import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.IGatewayMercadoPago
import java.lang.RuntimeException
import java.math.BigDecimal

@Component
class GatewayMercadoPago: IGatewayMercadoPago {
    override fun processarPagamento(pedidoId: Long, valor: BigDecimal) {
        //do nothing
//        throw RuntimeException("teste exc")
    }
}