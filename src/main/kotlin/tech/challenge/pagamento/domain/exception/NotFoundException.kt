package tech.challenge.pagamento.domain.exception

import java.lang.RuntimeException

class NotFoundException(message: String): RuntimeException(message)