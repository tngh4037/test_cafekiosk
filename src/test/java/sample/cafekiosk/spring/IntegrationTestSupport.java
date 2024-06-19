package sample.cafekiosk.spring;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.client.mail.MailSendClient;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockBean
    protected MailSendClient mailSendClient;
}

// 참고) 11:10
// 만약 다른 통합 테스트하는 쪽에서는 mailSendClient 를 사용하지 않고 싶다면(MockBean 처리를 하기 싫은 경우), IntegrationTestSupport 자체도 둘로 분리하는 것도 방법이다. ( 통합 테스트 환경을 분리하는 것 )
// - 하나는 @SpringBootTest 와 @Profile 등 순수 공통 환경을 모아둔 상위 클래스를 만들고
// - 다른 하나는 이를 상속받으면서, 내부에서 MockBean 처리를 하는 클래스를 만드는 것
//

