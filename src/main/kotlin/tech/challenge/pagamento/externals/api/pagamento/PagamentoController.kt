package tech.challenge.pagamento.externals.api.pagamento

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.challenge.pagamento.domain.pagamento.IPagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto

@Tag(name = "Pagamentos", description = "Api de pagamentos")
@RestController
@RequestMapping(value = ["/pagamentos"])
class PagamentoController {

    @Autowired
    lateinit var pagamentoService: IPagamentoService

    @GetMapping("/pedido/{pedido}")
    fun consultarStatusPagamento(@PathVariable("pedido") pedido: Long): PagamentoDto {
        return pagamentoService.consultarStatusPagamento(pedido)
    }
}