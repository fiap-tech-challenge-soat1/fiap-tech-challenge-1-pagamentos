package tech.challenge.pagamento.externals.api.pagamento.channel

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto
import java.util.logging.Logger

const val BINDING_NAME_CONFIRMAR_PAGAMENTO_TOPIC = "confirmarPagamentoSupplier"

@Component
class ConfirmarPagamentoChannel: IConfirmarPagamentoChannel {

    val logger: Logger = Logger.getLogger(ConfirmarPagamentoChannel::class.simpleName)

    @Autowired
    lateinit var streamBridge: StreamBridge

    override fun confirmarPagamento(resultadoPagamentoDto: ResultadoPagamentoDto) {
        streamBridge.send(BINDING_NAME_CONFIRMAR_PAGAMENTO_TOPIC, resultadoPagamentoDto)
        logger.info("[SERVICO-PAGAMENTO][TOPIC] - Mensagem enviada no topico confirmar-pagamento-topic ${Gson().toJson(resultadoPagamentoDto)}")
    }
}