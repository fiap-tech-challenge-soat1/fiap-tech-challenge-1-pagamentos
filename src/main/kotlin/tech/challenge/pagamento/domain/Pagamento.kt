package tech.challenge.pagamento.domain

import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.spring.data.firestore.Document

@Document(collectionName = "pagamentos")
class Pagamento {

    @DocumentId
    var id: String? = null
    var pedidoId: Long? = null
    var status: PagamentoStatus? = null

    fun toPagamentoDto(): PagamentoDto {
        return PagamentoDto(
            id = id!!,
            pedido = pedidoId,
            status = status
        )
    }

    companion object {
        fun createFrom(novoPagamentoRequestDto: NovoPagamentoRequestDto): Pagamento {
            return Pagamento().also {
                it.pedidoId = novoPagamentoRequestDto.pedido
                it.status = PagamentoStatus.PENDENTE
            }
        }
    }

}