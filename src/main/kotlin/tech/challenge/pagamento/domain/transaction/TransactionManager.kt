package tech.challenge.pagamento.domain.transaction

import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class TransactionManager {

    @Autowired
    lateinit var txManager: ReactiveFirestoreTransactionManager

    fun createNewTransactionalOperator(): TransactionalOperator {
        return DefaultTransactionDefinition().let {
            TransactionalOperator.create(txManager, it)
        }
    }
}