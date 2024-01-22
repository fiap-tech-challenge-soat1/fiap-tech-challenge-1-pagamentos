package tech.challenge.pagamento.domain.pagamento

import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

interface IPagamentoService {
    fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto
    fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto
    fun consultarStatusPagamento(pedido: Long): PagamentoDto
}