package tech.challenge.pagamento

import SpringIntegrationTest
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.AdditionalAnswers
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus
import java.math.BigDecimal
import java.util.function.Consumer


class PagamentoIntegrationTest: SpringIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var solicitarPagamentoConsumer: Consumer<NovoPagamentoRequestDto>

    private var resultActions: ResultActions? = null

    @Before
    fun before() {
        val transactionalOperator: TransactionalOperator = mock()
        `when`(transactionManager.createNewTransactionalOperator()).thenReturn(transactionalOperator)
        `when`(transactionalOperator.transactional(any<Mono<Pagamento>>())).then(AdditionalAnswers.returnsFirstArg<Mono<Pagamento>>())
    }

    @Given("o pedido do cliente não possui pagamento pendente ou já processado")
    fun o_pedido_do_cliente_não_possui_pagamento_pendente_ou_já_processado() {
        val monoEmpty = Mono.empty<Pagamento>()
        val pagamentoEntity = Mono.just(
            Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
                p.valorTotal = 10.50
            }
        )

        `when`(pagamentoRepository.save(any())).thenReturn(pagamentoEntity)
        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(monoEmpty)
    }

    @When("o servico de pagamento recebe a solicitacao")
    fun o_servico_de_pagamento_recebe_a_solicitacao() {
        solicitarPagamentoConsumer.accept(
            NovoPagamentoRequestDto(
                pedidoId = 172654,
                valorTotal = BigDecimal("10.50")
            )
        )
    }

    @Then("o pagamento e gerado")
    fun o_pagamento_e_gerado() {
        verify(pagamentoRepository, times(1)).save(any())
    }

    @Then("o cliente recebe o status code {int}")
    fun o_cliente_recebe_o_status_code(httpStatus: Int?) {
        assertEquals(HttpStatus.OK.value(), httpStatus)
    }

    @Then("o cliente recebe o pagamento vinculado ao pedido")
    fun o_cliente_recebe_o_pagamento_registrado() {
        assertEquals(
    "{\"id\":\"SvfAMoKJ65aPvl0oP2we\",\"pedido\":172654,\"status\":\"PENDENTE\"}",
            resultActions?.andReturn()?.response?.contentAsString
        )
    }

    @Given("o pedido do cliente possui um pagamento")
    fun o_pedido_do_cliente_possui_um_pagamento() {
        val pagamentoEntity = Mono.just(
            Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
                p.valorTotal = 10.50
            }
        )

        `when`(pagamentoRepository.findByPedidoIdAndStatusIn(
            pedidoId = 172654,
            status = listOf(
                PagamentoStatus.SUCESSO,
                PagamentoStatus.PENDENTE
            )
        )).thenReturn(pagamentoEntity)

        val fluxEmpty = Flux.just(
            Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
                p.valorTotal = 10.50
            }
        )

        `when`(pagamentoRepository.findAllByPedidoId(172654)).thenReturn(fluxEmpty)
    }

    @When("o cliente chama o \\/pagamentos\\/pedido\\/\\{pedido}")
    fun o_cliente_chama_o_pagamentos_pedido() {
        resultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/pedido/172654")
        )
    }
}