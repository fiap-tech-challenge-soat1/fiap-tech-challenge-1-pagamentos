package tech.challenge.pagamento.domain.pagamento

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.pagamento.entidade.ForcarErroGateway

@Repository
interface IForcarErroGatewayRepository: FirestoreReactiveRepository<ForcarErroGateway> {

    fun findByPedidoId(pedidoId: Long): Mono<ForcarErroGateway>
}