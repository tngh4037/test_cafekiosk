package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

/**
 * (스프링을 사용하지 않고) 순수 Mockito 로 검증해보기
 */
@ExtendWith(MockitoExtension.class) // @Mock 애노테이션을 사용해서 Mock 객체를 생성하기 위해서는 @ExtendWith(MockitoExtension.class) 를 적용해줘야 한다. ( 그래야 테스트가 시작될 때, 우리는 Mockito 를 사용해서 Mock 을 만들거야라는 것을 인지할 수 있다. )
class MailServiceTest {

    @Mock
    // @Spy // 일부만 stubbing 하고, 나머지는 실제 객체가 그대로 동작하도록 하고자 할 때
    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks // MailService 의 생성자를 보고 Mock 객체로 선언된 애들을 inject 해준다.
    private MailService mailService;

    @DisplayName("메일 전송 테스트1")
    @Test
    public void sendMail1() throws Exception {
        // given
        MailSendClient mailSendClient = Mockito.mock(MailSendClient.class);
        MailSendHistoryRepository mailSendHistoryRepository = Mockito.mock(MailSendHistoryRepository.class);
        MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);
        // stubbing
        Mockito.when(mailSendClient.sendEmail(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class)); // mailSendHistoryRepository 의 save 라는 행위가 1번 불렸는지를 검증
    }

    @DisplayName("메일 전송 테스트2")
    @Test
    public void sendMail2() throws Exception {

        // given
        Mockito.doReturn(true)
                .when(mailSendClient)
                .sendEmail(
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

    @DisplayName("메일 전송 테스트3 - BDDMockito")
    @Test
    public void sendMail3() throws Exception {

        // given
        // Mockito.when(mailSendClient.sendEmail(
        //                 ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
        //                 ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        //         .thenReturn(true);
        BDDMockito.given(mailSendClient.sendEmail(
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .willReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

}