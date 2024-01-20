package tech.challenge.pagamento.domain

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PagamentoRepository: MongoRepository<Pagamento, String> {

    @Query(
        """
            {
                pedidoId: ?0,
                status: { 
                    "${'$'}in" : [
                      "SUCESSO",
                      "PENDENTE"
                    ]
                }
            }
        """,
        exists = true
    )
    fun existePagamentoRealizadoOuPendenteParaPedidoId(pedidoId: Long): Boolean

    @Query(
        """
            { 
                pedidoId: ?0,
                status: "PENDENTE"
            }
        """,
    )
    fun encontrarPagamentoPendenteParaPedidoId(pedidoId: Long): Pagamento?

    @Query(
        """
            {
                pedidoId: ?0
            }
        """
    )
    fun encontrarPagamentoParaPedidoId(pedidoId: Long): Pagamento?
}