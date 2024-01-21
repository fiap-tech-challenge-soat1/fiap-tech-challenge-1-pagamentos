package tech.challenge.pagamento.domain.pedido

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import tech.challenge.pagamento.domain.pagamento.entidade.PagamentoStatus

@FeignClient(value = "pedidos", url = "\${pagamento.feign.pedido.url}")
interface IPedidoResource {

    @RequestMapping(method = [RequestMethod.GET], value = ["/clientes"])
    fun listarClientes(): String?// Teste

    @PostMapping("/pedidos/{pedido}/confirmar-pagamento")
    fun confirmarPagamento(@PathVariable pedido: Long, pagamentoStatus: PagamentoStatus)
}