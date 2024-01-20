package tech.challenge.pagamento.domain

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PagamentoRepository: FirestoreReactiveRepository<Pagamento> {

//    @Query(
//        """
//            {
//                pedidoId: ?0,
//                status: {
//                    "${'$'}in" : [
//                      "SUCESSO",
//                      "PENDENTE"
//                    ]
//                }
//            }
//        """,
//        exists = true
//    )
//    fun existePagamentoRealizadoOuPendenteParaPedidoId(pedidoId: Long): Boolean
//
//    @Query(
//        """
//            {
//                pedidoId: ?0,
//                status: "PENDENTE"
//            }
//        """,
//    )
//    fun encontrarPagamentoPendenteParaPedidoId(pedidoId: Long): Pagamento?

//    @Query(
//        """
//            {
//                pedidoId: ?0
//            }
//        """
//    )
//    fun encontrarPagamentoParaPedidoId(pedidoId: Long): Pagamento?

//    fun existsByPedidoIdAndStatusIn(pedido: Long, status: List<PagamentoStatus>): Mono<Boolean>

    fun findByPedidoId(pedidoId: Long): Mono<Pagamento?>
}