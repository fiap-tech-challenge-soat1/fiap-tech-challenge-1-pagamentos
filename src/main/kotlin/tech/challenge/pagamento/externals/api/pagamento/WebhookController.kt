package tech.challenge.pagamento.externals.api.pagamento

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tech.challenge.pagamento.domain.pagamento.IPagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

@Tag(name = "Webhook", description = "Weebhook para confirmação de pagamento")
@RestController
class WebhookController {

    @Autowired
    lateinit var pagamentoService: IPagamentoService

    @PostMapping("/webhooks/pagamentos-confirmados/{pedido}")
    fun webhook(@PathVariable("pedido") pedido: Long, @RequestBody status: PagamentoStatus): PagamentoDto {
        return pagamentoService.confirmarPagamento(pedido, status)
    }
}