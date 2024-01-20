package tech.challenge.pagamento.domain

import org.springframework.data.annotation.Id

class Pagamento(
    @Id
    var id: String? = null,
    var pedidoId: Long,
    var status: PagamentoStatus
) {

    fun toPagamentoDto(): PagamentoDto {
        return PagamentoDto(
            id = id!!,
            pedido = pedidoId,
            status = status
        )
    }

    companion object {
        fun createFrom(novoPagamentoRequestDto: NovoPagamentoRequestDto): Pagamento {
            return Pagamento(
                pedidoId = novoPagamentoRequestDto.pedido,
                status = PagamentoStatus.PENDENTE
            )
        }
    }

}