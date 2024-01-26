package tech.challenge.pagamento.domain.pagamento

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

@Repository
interface IPagamentoRepository: FirestoreReactiveRepository<Pagamento> {
    fun findAllByPedidoId(pedidoId: Long): Flux<Pagamento>

    fun findByPedidoIdAndStatusIn(pedidoId: Long, status: List<PagamentoStatus>): Mono<Pagamento>
}