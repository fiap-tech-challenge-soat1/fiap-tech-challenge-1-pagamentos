package tech.challenge.pagamento.domain.pagamento.entidade

import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.spring.data.firestore.Document

@Document(collectionName = "forcar-erro-gateway")
class ForcarErroGateway {

    @DocumentId
    var id: String? = null
    var pedidoId: Long? = null
}