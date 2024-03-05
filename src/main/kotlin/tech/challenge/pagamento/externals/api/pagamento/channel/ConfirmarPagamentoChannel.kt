package tech.challenge.pagamento.externals.api.pagamento.channel

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto
import java.util.function.Supplier

@Component
class ConfirmarPagamentoChannel: IConfirmarPagamentoChannel {
    private val sink: Sinks.Many<ResultadoPagamentoDto> = Sinks.many().multicast().onBackpressureBuffer()

    @Bean
    private fun confirmarPagamentoSupplier(): Supplier<Flux<ResultadoPagamentoDto>> {
        return Supplier { sink.asFlux() }
    }

    override fun confirmarPagamento(resultadoPagamentoDto: ResultadoPagamentoDto) {
        sink.tryEmitNext(resultadoPagamentoDto)
    }
}