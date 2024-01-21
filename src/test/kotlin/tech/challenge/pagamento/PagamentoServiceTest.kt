package tech.challenge.pagamento

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException
import tech.challenge.pagamento.domain.pagamento.IPagamentoRepository
import tech.challenge.pagamento.domain.pagamento.PagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import tech.challenge.pagamento.domain.pedido.IPedidoResource

class PagamentoServiceTest {

    private val pagamentoRepository: IPagamentoRepository = mock()
    private val pedidoResource: IPedidoResource = mock()
    private val pagamentoService: PagamentoService = PagamentoService().also {
        it.pagamentoRepository = pagamentoRepository
        it.pedidoResource = pedidoResource
    }

    @Test
    fun quandoJaExisteJaExisterUmPagamentoParaUmPedidoDeveLancarExceptionAoTentarRegistrarOutro() {
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(
                Pagamento().also { p ->
                    p.id = "SvfAMoKJ65aPvl0oP2we"
                    p.pedidoId = 172654
                    p.status = PagamentoStatus.PENDENTE
                }
            )
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntity)

        val exception = assertThrows<BusinessException> {
            pagamentoService.processarPagamento(NovoPagamentoRequestDto(
                pedido = 172654
            ))
        }

        assertEquals("Pedido já possui pagamento realizado ou em processamento", exception.message)
    }

    @Test
    fun quandoPagamentoNaoEncontradoAoConfirmarPagamentoDeveLancarNotFoundExceptionException() {
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(null)
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntity)

        val exception = assertThrows<NotFoundException> {
            pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)
        }

        assertEquals("Não foi encontrado pagamento pendente para o pedido", exception.message)
    }

    @Test
    fun quandoPagamentoNaoEstaPendenteAoConfirmarPagamentoDeveLancarException() {
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(
                Pagamento().also { p ->
                    p.id = "SvfAMoKJ65aPvl0oP2we"
                    p.pedidoId = 172654
                    p.status = PagamentoStatus.SUCESSO
                }
            )
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntity)

        val exception = assertThrows<BusinessException> {
            pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)
        }

        assertEquals("O pagamento desse pedido não esta mais pendente", exception.message)
    }

    @Test
    fun deveNotificarPedidoQuandoPagamentoConfirmado() {
        val pagamentoEntity = Pagamento().also { p ->
            p.id = "SvfAMoKJ65aPvl0oP2we"
            p.pedidoId = 172654
            p.status = PagamentoStatus.PENDENTE
        }
        val pagamentoEntityMono: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(
                pagamentoEntity
            )
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntityMono)
        `when`(pagamentoRepository.save(pagamentoEntity)).thenReturn(pagamentoEntityMono)

        val pagamentoDto = pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)

        assertEquals("SvfAMoKJ65aPvl0oP2we", pagamentoDto.id)
        assertEquals(172654, pagamentoDto.pedido)
        assertEquals(PagamentoStatus.SUCESSO, pagamentoDto.status)

        verify(pedidoResource).confirmarPagamento(172654, PagamentoStatus.SUCESSO)
    }

    @Test
    fun quandoPagamentoNaoExisteAoConsultarStatusDeveLancarNotFoundException() {
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(null)
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntity)

        val exception = assertThrows<NotFoundException> {
            pagamentoService.consultarStatusPagamento(172654)
        }

        assertEquals("Não foi encontrado pagamento para o pedido", exception.message)
    }
}