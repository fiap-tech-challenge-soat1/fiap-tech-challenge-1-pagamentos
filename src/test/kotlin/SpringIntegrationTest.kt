import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import tech.challenge.pagamento.PagamentoApplication
import tech.challenge.pagamento.domain.pagamento.IPagamentoRepository

@CucumberContextConfiguration
@SpringBootTest(classes = [PagamentoApplication::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SpringIntegrationTest {
    @MockBean
    @Autowired
    lateinit var pagamentoRepository: IPagamentoRepository
}