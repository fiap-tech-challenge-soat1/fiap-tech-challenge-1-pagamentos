package tech.challenge.pagamento.externals.api.pagamento.channel

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.IPagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import java.util.function.Consumer
import java.util.logging.Logger

@Component
class SolicitarPagamentoChannel {

    val logger: Logger = Logger.getLogger(SolicitarPagamentoChannel::class.simpleName)

    @Autowired
    lateinit var pagamentoService: IPagamentoService

    @Bean
    private fun solicitarPagamentoConsumer(): Consumer<NovoPagamentoRequestDto> {
        return Consumer {
            logger.info("Message received: ${it.pedidoId}")
            pagamentoService.processarPagamento(it)
        }
    }
}