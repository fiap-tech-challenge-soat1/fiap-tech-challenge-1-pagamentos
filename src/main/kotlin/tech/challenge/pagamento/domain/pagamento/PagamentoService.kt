package tech.challenge.pagamento.domain.pagamento

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import tech.challenge.pagamento.domain.pedido.IPedidoResource

@Service
class PagamentoService: IPagamentoService {

    @Autowired
    lateinit var pagamentoRepository: IPagamentoRepository

    @Autowired
    lateinit var pedidoResource: IPedidoResource

    override fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        pagamentoRepository.findByPedidoId(novoPagamentoRequestDto.pedido).block()?.run {
            throw BusinessException("Pedido já possui pagamento realizado ou em processamento")
        }

        return Pagamento.createFrom(novoPagamentoRequestDto).let {
            pagamentoRepository.save(it).block()!!
        }.toPagamentoDto()
    }

    override fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto {
        val pagamento  = pagamentoRepository.findByPedidoId(pedido).block() ?: throw NotFoundException("Não foi encontrado pagamento pendente para o pedido")

        if(pagamento.status != PagamentoStatus.PENDENTE) {
            throw BusinessException("O pagamento desse pedido não esta mais pendente")
        }

        pagamento.status = status
        return pagamentoRepository.save(pagamento).block()!!.toPagamentoDto().also {
            pedidoResource.confirmarPagamento(pedido, status)
        }
    }

    override fun consultarStatusPagamento(pedido: Long): PagamentoDto {
        pedidoResource.listarClientes()?.run {
            println(this)
        }
        return pagamentoRepository.findByPedidoId(pedido).block()?.toPagamentoDto() ?: throw NotFoundException("Não foi encontrado pagamento para o pedido")
    }
}