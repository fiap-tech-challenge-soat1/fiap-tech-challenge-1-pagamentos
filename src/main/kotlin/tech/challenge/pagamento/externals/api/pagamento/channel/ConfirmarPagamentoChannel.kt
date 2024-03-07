package tech.challenge.pagamento.externals.api.pagamento.channel

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto

const val BINDING_NAME_CONFIRMAR_PAGAMENTO_TOPIC = "confirmarPagamentoSupplier"

@Component
class ConfirmarPagamentoChannel: IConfirmarPagamentoChannel {

    @Autowired
    lateinit var streamBridge: StreamBridge

    override fun confirmarPagamento(resultadoPagamentoDto: ResultadoPagamentoDto) {
        streamBridge.send(BINDING_NAME_CONFIRMAR_PAGAMENTO_TOPIC, resultadoPagamentoDto)
    }
}