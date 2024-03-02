package tech.challenge.pagamento.externals.api.pagamento.channel

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tech.challenge.pagamento.domain.pagamento.IPagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import java.util.function.Consumer

@Component
class SolicitarPagamentoChannel {

    @Autowired
    lateinit var pagamentoService: IPagamentoService

    @Bean
    private fun solicitarPagamentoConsumer(): Consumer<NovoPagamentoRequestDto> {
        return Consumer {
            println("${it.pedidoId} - ${it.valorTotal}")
            pagamentoService.processarPagamento(it)
        }
    }
}