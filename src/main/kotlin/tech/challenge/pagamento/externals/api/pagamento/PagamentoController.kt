package tech.challenge.pagamento.externals.api.pagamento

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.challenge.pagamento.domain.pagamento.IPagamentoService
import tech.challenge.pagamento.domain.pagamento.dto.NovoPagamentoRequestDto
import tech.challenge.pagamento.domain.pagamento.dto.PagamentoDto

@RestController
@RequestMapping("/")
class PagamentoController {

    @Autowired
    lateinit var pagamentoService: IPagamentoService

    @PostMapping
    fun processarPagamento(@RequestBody novoPagamentoRequestDto: NovoPagamentoRequestDto): PagamentoDto {
        return pagamentoService.processarPagamento(novoPagamentoRequestDto)
    }

    @GetMapping("/pedido/{pedido}")
    fun consultarStatusPagamento(@PathVariable("pedido") pedido: Long): PagamentoDto {
        return pagamentoService.consultarStatusPagamento(pedido)
    }
}