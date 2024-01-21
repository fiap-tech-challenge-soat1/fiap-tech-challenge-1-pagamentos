package tech.challenge.pagamento

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class PagamentoApplication

fun main(args: Array<String>) {
	runApplication<PagamentoApplication>(*args)
}
