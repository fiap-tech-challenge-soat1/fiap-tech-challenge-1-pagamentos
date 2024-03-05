package tech.challenge.pagamento.externals.api.pagamento.channel

import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto

interface IConfirmarPagamentoChannel {
    fun confirmarPagamento(resultadoPagamentoDto: ResultadoPagamentoDto)
}