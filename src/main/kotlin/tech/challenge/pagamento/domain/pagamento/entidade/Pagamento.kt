package tech.challenge.pagamento.domain.pagamento.entidade

import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.spring.data.firestore.Document
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto

@Document(collectionName = "pagamentos")
class Pagamento {

    @DocumentId
    var id: String? = null
    var pedidoId: Long? = null
    var valorTotal: Double? = null
    var status: PagamentoStatus? = null
    var createdAt: Timestamp = Timestamp.now()

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
                it.pedidoId = novoPagamentoRequestDto.pedidoId
                it.status = PagamentoStatus.PENDENTE
                it.valorTotal = novoPagamentoRequestDto.valorTotal.toDouble()
            }
        }
    }

}