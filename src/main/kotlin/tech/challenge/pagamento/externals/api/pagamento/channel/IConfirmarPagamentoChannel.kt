package tech.challenge.pagamento.externals.api.pagamento.channel

import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto

interface IConfirmarPagamentoChannel {
    fun confirmarPagamento(pagamentoDto: PagamentoDto)
}