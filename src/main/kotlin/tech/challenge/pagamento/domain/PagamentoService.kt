package tech.challenge.pagamento.domain

import org.apache.commons.lang3.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException

@Service
class PagamentoService {

    @Autowired
    lateinit var pagamentoRepository: PagamentoRepository

    fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        pagamentoRepository.findByPedidoId(novoPagamentoRequestDto.pedido).block()?.run {
            throw BusinessException("Pedido já possui pagamento realizado ou em processamento")
        }

        return Pagamento.createFrom(novoPagamentoRequestDto).also {
            pagamentoRepository.save(it).block()
        }.toPagamentoDto()
    }

    fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto {
        val pagamento  = pagamentoRepository.findByPedidoId(pedido).block() ?: throw NotFoundException("Não foi encontrado pagamento pendente para o pedido")

        if(pagamento.status != PagamentoStatus.PENDENTE) {
            throw BusinessException("O pagamento desse pedido não esta mais pendente")
        }

        pagamento.status = status
        return pagamentoRepository.save(pagamento).block()!!.toPagamentoDto()

        //TODO notificar serviço de pedido
    }

    fun consultarStatusPagamento(pedido: Long): PagamentoDto {
        return pagamentoRepository.findByPedidoId(pedido).block()?.toPagamentoDto() ?: throw NotFoundException("Não foi encontrado pagamento para o pedido")
    }
}