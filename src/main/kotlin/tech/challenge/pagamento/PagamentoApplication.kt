package tech.challenge.pagamento

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
class PagamentoApplication

fun main(args: Array<String>) {
	runApplication<PagamentoApplication>(*args)
}
