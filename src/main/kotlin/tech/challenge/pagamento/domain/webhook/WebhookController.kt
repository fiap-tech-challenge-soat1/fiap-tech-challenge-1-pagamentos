package tech.challenge.pagamento.domain.webhook

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tech.challenge.pagamento.domain.PagamentoDto
import tech.challenge.pagamento.domain.PagamentoService
import tech.challenge.pagamento.domain.PagamentoStatus

@RestController
class WebhookController {

    @Autowired
    lateinit var pagamentoService: PagamentoService

    @PostMapping("/webhooks/pagamentos-confirmados/{pedido}")
    fun webhook(@PathVariable("pedido") pedido: Long, @RequestBody status: PagamentoStatus): PagamentoDto {
        return pagamentoService.confirmarPagamento(pedido, status)
    }
}