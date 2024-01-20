package tech.challenge.pagamento.domain.exception

import java.lang.RuntimeException

class BusinessException(message: String): RuntimeException(message)