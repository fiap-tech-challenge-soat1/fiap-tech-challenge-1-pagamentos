package tech.challenge.pagamento.externals.api.pagamento.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.IForcarErroGatewayRepository
import tech.challenge.pagamento.domain.pagamento.IGatewayMercadoPago
import java.math.BigDecimal

private const val ID_PEDIDO_PARA_FALHAR = "ID_PEDIDO_PARA_FALHAR"

@Component
class GatewayMercadoPago: IGatewayMercadoPago {

    @Autowired
    lateinit var forcarErrorGatewayRepository: IForcarErroGatewayRepository

    override fun processarPagamento(pedidoId: Long, valor: BigDecimal) {
        forcarErrorGatewayRepository.findByPedidoId(pedidoId).block()?.run {
            println("Fingindo problema com o gateway")
            throw RuntimeException("Falha na solicitação de pagamento")
        }
    }
}