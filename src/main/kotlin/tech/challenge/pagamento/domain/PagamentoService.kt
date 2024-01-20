package tech.challenge.pagamento.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException

@Service
class PagamentoService {

    @Autowired
    lateinit var pagamentoRepository: PagamentoRepository

    fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        if(pagamentoRepository.existePagamentoRealizadoOuPendenteParaPedidoId(novoPagamentoRequestDto.pedido)) {
            throw BusinessException("Pedido já possui pagamento realizado ou em processamento")
        }

        return Pagamento.createFrom(novoPagamentoRequestDto).also {
            pagamentoRepository.save(it)
        }.toPagamentoDto()
    }

    fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto {
        return pagamentoRepository.encontrarPagamentoPendenteParaPedidoId(pedido)?.let {
            it.status = status
            pagamentoRepository.save(it).toPagamentoDto()
        } ?: throw NotFoundException("Não foi encontrado pagamento pendente para o pedido")

        //TODO notificar serviço de pedido
    }

    fun consultarStatusPagamento(pedido: Long): PagamentoDto {
        return pagamentoRepository.encontrarPagamentoParaPedidoId(pedido)?.toPagamentoDto() ?: throw NotFoundException("Não foi encontrado pagamento para o pedido")
    }
}