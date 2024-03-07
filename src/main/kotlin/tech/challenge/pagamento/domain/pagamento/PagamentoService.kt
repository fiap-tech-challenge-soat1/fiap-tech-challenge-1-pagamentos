package tech.challenge.pagamento.domain.pagamento

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import tech.challenge.pagamento.domain.transaction.TransactionManager
import tech.challenge.pagamento.externals.api.pagamento.channel.ConfirmarPagamentoChannel

@Service
class PagamentoService: IPagamentoService {

    @Autowired
    lateinit var pagamentoRepository: IPagamentoRepository

    @Autowired
    lateinit var confirmarPagamentoChannel: ConfirmarPagamentoChannel

    @Autowired
    lateinit var gatewayMercadoPago: IGatewayMercadoPago

    @Autowired
    lateinit var transactionManager: TransactionManager

    override fun processarPagamento(novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        val transactionOperator = transactionManager.createNewTransactionalOperator()

        pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = novoPagamentoRequestDto.pedidoId,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        ).`as`(transactionOperator::transactional).block()?.run {
            throw BusinessException("Pedido já possui pagamento realizado ou em processamento")
        }

        return Pagamento.createFrom(novoPagamentoRequestDto).let { novoPagamento ->
            pagamentoRepository.save(novoPagamento).doOnNext {
                solicitarPagamentoAoGateway(it)
            }.map { pagamento ->
                pagamento.toPagamentoDto()
            }.doOnNext {
                println("Novo pagamento ${it.id} criado para o pedido ${it.pedido}")
            }.doOnError {
                acaoDeContornoFalhaAoSolicitarPagamento(novoPagamento.pedidoId!!)
            }.`as`(transactionOperator::transactional).block()!!
        }
    }

    private fun solicitarPagamentoAoGateway(pagamento: Pagamento) {
        gatewayMercadoPago.processarPagamento(
            pagamento.pedidoId!!,
            pagamento.valorTotal!!.toBigDecimal()
        )
    }

    private fun acaoDeContornoFalhaAoSolicitarPagamento(pedidoId: Long) {
        confirmarPagamentoChannel.confirmarPagamento(
            ResultadoPagamentoDto(
                pedido = pedidoId,
                resultadoPagamento = PagamentoStatus.FALHOU
            )
        )
    }

    override fun confirmarPagamento(pedido: Long, status: PagamentoStatus): PagamentoDto {
        val transactionOperator = transactionManager.createNewTransactionalOperator()
        val pagamento = pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = pedido,
            status = listOf(
                PagamentoStatus.PENDENTE
            )
        ).`as`(transactionOperator::transactional).block()?.also {
            it.status = status
        } ?: throw NotFoundException("Não foi encontrado pagamento pendente para o pedido")

        return pagamentoRepository.save(pagamento).map {
            it.toPagamentoDto()
        }.doOnNext {
            confirmarPagamentoChannel.confirmarPagamento(it.toResultadoPagamentoDto())
        }.`as`(transactionOperator::transactional).block()!!
    }

    override fun consultarStatusPagamento(pedido: Long): PagamentoDto {
        val transactionOperator = transactionManager.createNewTransactionalOperator()
        val pagamentoCorrente = pagamentoRepository
            .findAllByPedidoId(pedido)
            .collectList()
            .`as`(transactionOperator::transactional)
            .block()?.maxByOrNull { it.createdAt }

        if(pagamentoCorrente == null) {
            throw NotFoundException("Não foi encontrado pagamento para o pedido")
        }

        return pagamentoCorrente.toPagamentoDto()
    }
}