package tech.challenge.pagamento

import SpringIntegrationTest
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import reactor.core.publisher.Mono
import tech.challenge.pagamento.domain.pagamento.entidade.Pagamento
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus


class PagamentoIntegrationTest: SpringIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private var resultActions: ResultActions? = null

    @Given("o pedido do cliente não possui pagamento pendente ou já processado")
    fun o_pedido_do_cliente_não_possui_pagamento_pendente_ou_já_processado() {
        val monoEmpty: Mono<Pagamento> = mock<Mono<Pagamento>>().also {
            `when`(it.block()).thenReturn(null)
        }
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>>().also {
            `when`(it.block()).thenReturn(Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
            })
        }
        `when`(pagamentoRepository.save(any())).thenReturn(pagamentoEntity)
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(monoEmpty)
    }

    @When("o cliente chama \\/pagamentos")
    fun o_cliente_chama_pagamentos() {
        resultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "pedido": 172654
                    }
                    """
                )
        )
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
        val pagamentoEntity: Mono<Pagamento> = mock<Mono<Pagamento>>().also {
            `when`(it.block()).thenReturn(Pagamento().also { p ->
                p.id = "SvfAMoKJ65aPvl0oP2we"
                p.pedidoId = 172654
                p.status = PagamentoStatus.PENDENTE
            })
        }
        `when`(pagamentoRepository.findByPedidoId(172654)).thenReturn(pagamentoEntity)
    }

    @When("o cliente chama o \\/pagamentos\\/pedido\\/\\{pedido}")
    fun o_cliente_chama_o_pagamentos_pedido() {
        resultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/pagamentos/pedido/172654")
        )
    }
}