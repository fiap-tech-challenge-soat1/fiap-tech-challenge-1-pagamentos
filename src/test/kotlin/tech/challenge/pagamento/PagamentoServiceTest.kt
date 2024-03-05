package tech.challenge.pagamento

import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.exception.BusinessException
import tech.challenge.pagamento.domain.exception.NotFoundException
import tech.challenge.pagamento.domain.pagamento.IPagamentoRepository
import tech.challenge.pagamento.domain.pagamento.PagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.ResultadoPagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import tech.challenge.pagamento.domain.pedido.IPedidoResource
import java.math.BigDecimal

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
        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntity)

        val exception = assertThrows<BusinessException> {
            pagamentoService.processarPagamento(NovoPagamentoRequestDto(
                pedidoId = 172654,
                valorTotal = BigDecimal.TEN
            ))
        }

        assertEquals("Pedido já possui pagamento realizado ou em processamento", exception.message)
    }

    @Test
    fun quandoPagamentoNaoEncontradoAoConfirmarPagamentoDeveLancarNotFoundExceptionException() {
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>?>().also {
            `when`(it.block()).thenReturn(null)
        }
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

    @Ignore
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
        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntityMono)
        `when`(pagamentoRepository.save(pagamentoEntity)).thenReturn(pagamentoEntityMono)

        val pagamentoDto = pagamentoService.confirmarPagamento(172654, PagamentoStatus.SUCESSO)

        assertEquals("SvfAMoKJ65aPvl0oP2we", pagamentoDto.id)
        assertEquals(172654, pagamentoDto.pedido)
        assertEquals(PagamentoStatus.SUCESSO, pagamentoDto.status)

        verify(pedidoResource).confirmarPagamento(172654, ResultadoPagamentoDto(PagamentoStatus.SUCESSO))
    }

    @Test
    fun quandoPagamentoNaoExisteAoConsultarStatusDeveLancarNotFoundException() {
        val fluxEmpty: Flux<Pagamento> = mock<Flux<Pagamento>>().also {
            val monoList: Mono<List<Pagamento>> = mock<Mono<List<Pagamento>>?>().also { mono ->
                `when`(mono.block()).thenReturn(emptyList())
            }
            `when`(it.collectList()).thenReturn(monoList)
        }

        `when`(pagamentoRepository.findAllByPedidoId(172654)).thenReturn(fluxEmpty)

        val exception = assertThrows<NotFoundException> {
            pagamentoService.consultarStatusPagamento(172654)
        }

        assertEquals("Não foi encontrado pagamento para o pedido", exception.message)
    }
}