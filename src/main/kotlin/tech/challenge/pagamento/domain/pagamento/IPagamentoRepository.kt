package tech.challenge.pagamento.domain.pagamento

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento

@Repository
interface IPagamentoRepository: FirestoreReactiveRepository<Pagamento> {
    fun findByPedidoId(pedidoId: Long): Mono<Pagamento>
}