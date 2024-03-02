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
import tech.challenge.pagamento.externals.api.pagamento.channel.ConfirmarPagamentoChannel

@Service
class PagamentoService: IPagamentoService {

    @Autowired
    lateinit var pagamentoRepository: IPagamentoRepository

    @Autowired
    lateinit var pedidoResource: IPedidoResource

    @Autowired
    lateinit var confirmarPagamentoChannel: ConfirmarPagamentoChannel

    override fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = novoPagamentoRequestDto.pedidoId,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        ).block()?.run {
            throw BusinessException("Pedido já possui pagamento realizado ou em processamento")
        }

        return Pagamento.createFrom(novoPagamentoRequestDto).let {
            pagamentoRepository.save(it).block()!!
        }.toPagamentoDto()
    }

    override fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto {
        val pagamento = pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = pedido,
            status = listOf(
                PagamentoStatus.PENDENTE
            )
        ).block() ?: throw NotFoundException("Não foi encontrado pagamento pendente para o pedido")

        pagamento.status = status
        return pagamentoRepository.save(pagamento).block()!!.toPagamentoDto().also {
            confirmarPagamentoChannel.confirmarPagamento(it)
        }
    }

    override fun consultarStatusPagamento(pedido: Long): PagamentoDto {
        val pagamentoCorrente = pagamentoRepository.findAllByPedidoId(pedido).collectList().block()?.takeIf { it.isNotEmpty() }?.maxBy { it.createdAt }

        if(pagamentoCorrente == null) {
            throw NotFoundException("Não foi encontrado pagamento para o pedido")
        }

        return pagamentoCorrente.toPagamentoDto()
    }
}