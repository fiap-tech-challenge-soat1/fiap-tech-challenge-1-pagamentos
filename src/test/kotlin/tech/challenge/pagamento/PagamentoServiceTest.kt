package tech.challenge.pagamento

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.AdditionalAnswers
import org.mockito.Mockito.*
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.exception.NotFoundException
import tech.challenge.pagamento.domain.pagamento.IPagamentoRepository
import tech.challenge.pagamento.domain.pagamento.PagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import tech.challenge.pagamento.domain.transaction.TransactionManager
import tech.challenge.pagamento.externals.api.pagamento.channel.ConfirmarPagamentoChannel
import tech.challenge.pagamento.externals.api.pagamento.gateway.GatewayMercadoPago
import java.math.BigDecimal

class PagamentoServiceTest {

    private val pagamentoRepository: IPagamentoRepository = mock()
    private val transactionManager: TransactionManager = mock()
    private val confirmarPagamentoChannel: ConfirmarPagamentoChannel = ConfirmarPagamentoChannel().also {
        it.streamBridge = mock()
    }
    private val gatewayMercadoPago: GatewayMercadoPago = mock()
    private val pagamentoService = PagamentoService().also {
        it.pagamentoRepository = pagamentoRepository
        it.confirmarPagamentoChannel = confirmarPagamentoChannel
        it.gatewayMercadoPago = gatewayMercadoPago
        it.transactionManager = transactionManager
    }

    @Before
    fun before() {
        val transactionalOperator: TransactionalOperator = mock()
        `when`(transactionManager.createNewTransactionalOperator()).thenReturn(transactionalOperator)
        `when`(transactionalOperator.transactional(any<Mono<Pagamento>>())).then(AdditionalAnswers.returnsFirstArg<Mono<Pagamento>>())
    }

    @Test
    fun quandoJaExisteJaExistirUmPagamentoParaUmPedidoDeveNotificarFalhaAoTentarRegistrarOutro() {
        val pagamentoEntity = Mono.just(
            Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
            }
        )

        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntity)

        pagamentoService.processarPagamento(
            NovoPagamentoRequestDto(
                pedidoId = 172654,
                valorTotal = BigDecimal.TEN
            )
        )
    }

    @Test
    fun quandoPagamentoNaoEncontradoAoConfirmarPagamentoDeveLancarNotFoundException() {
        val pagamentoEntity = Mono.empty<Pagamento>()

        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntity)

        val exception = assertThrows<NotFoundException> {
            pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)
        }

        assertEquals("Não foi encontrado pagamento pendente para o pedido", exception.message)
    }

    @Test
    fun deveNotificarPedidoQuandoPagamentoConfirmado() {
        val pagamentoEntity = Pagamento().also { p ->
            p.id = "SvfAMoKJ65aPvl0oP2we"
            p.pedidoId = 172654
            p.status = PagamentoStatus.PENDENTE
        }
        val pagamentoEntityMono = Mono.just(pagamentoEntity)

        `when`(pagamentoRepository.save(pagamentoEntity)).thenReturn(pagamentoEntityMono)
        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntityMono)

        val pagamentoDto = pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)

        assertEquals("SvfAMoKJ65aPvl0oP2we", pagamentoDto.id)
        assertEquals(172654, pagamentoDto.pedido)
        assertEquals(PagamentoStatus.SUCESSO, pagamentoDto.status)

        verify(confirmarPagamentoChannel.streamBridge, times(1)).send(any(), any())
    }

    @Test
    fun quandoPagamentoNaoExisteAoConsultarStatusDeveLancarNotFoundException() {
        val fluxEmpty = Flux.empty<Pagamento>()

        `when`(pagamentoRepository.findAllByPedidoId(172654)).thenReturn(fluxEmpty)

        val exception = assertThrows<NotFoundException> {
            pagamentoService.consultarStatusPagamento(172654)
        }

        assertEquals("Não foi encontrado pagamento para o pedido", exception.message)
    }

    @Test
    fun deveExecutarAcaoDeContornoQuandoNaoForPossivelSolicitarPagamento() {

    }
}