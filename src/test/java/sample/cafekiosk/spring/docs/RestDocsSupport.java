package sample.cafekiosk.spring.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        // RestDoc 을 작성하기 위한 mockMvc 설정 ( mockMvc 에 대해서 RestDocumentation 에 대한 설정 )
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController()) // standaloneSetup(문서로 만들고 싶은 컨트롤러를 넣어준다.)
                .apply(documentationConfiguration(provider))
                .build();
    }

    protected abstract Object initController();
}

// 강의에서는 스프링 의존성 없이 mockMvc 를 standalone 으로 만들어서 띄우고 있다. (스프링과 무관한 컨트롤러에 대한 단위테스트)