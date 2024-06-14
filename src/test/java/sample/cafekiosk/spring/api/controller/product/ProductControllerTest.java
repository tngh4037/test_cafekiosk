package sample.cafekiosk.spring.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

// 서비스 레이어 하위로는 다 mocking 처리를 한다. -> mocking 처리를 도와주는 테스트 프레임워크: MockMvc
// 그리고 MockMvc 를 사용하려면 @WebMvcTest 라는 애가 필요하다. ( @SpringBootTest 는 전체 빈 컨텍스트를 다 띄우는 어노테이션이라면, @WebMvcTest 는 컨트롤러 관련된 빈들만 올릴 수 있는 가벼운 테스트 어노테이션 이라고 생각하자. )
@WebMvcTest(controllers = ProductController.class) // 테스트 하고자 하는 컨트롤러를 명시해주면 된다.
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // objectMapper : (json <-> object) 간 직렬화 / 역직렬화를 도와주는 친구

    // @Mock, @MockBean 은 mockito 라는 라이브러리의 애노테이션이다. ( mockito 라는 라이브러리는 스프링부트 스타터 테스트를 사용하는 경우, 자동으로 포함되어 있다. )
    @MockBean // @MockBean 은 컨테이너에 mockito 로 만든 Mock 객체를 넣어주는 역할을 한다. ( ProductService 는 이미 빈으로 관리되고 있다. 빈에 적용하면, ProductService 대신 ProductService Mock 객체를 대신 컨테이너에 넣어준다. )
    private ProductService productService;

    /**
     * 컨트롤러 테스트
     * - 1) 요청이 정상적으로 처리되는가 ? (요청 자체에 대한 검증)
     * - 2) 요청 파라미터가 유효한지 검증하고 있는가 ? (파라미터에 대한 검증)
     */
    // 1) 요청이 잘 되는지 체크
    @DisplayName("신규 상품을 등록한다.")
    @Test
    public void createProduct() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new") // perform() 을 통해 api 를 쏘는 수행을 나타낸다.
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()) // 요청이 어떻게 날라갔는지 상세히 확인할 수 있다.
                .andExpect(MockMvcResultMatchers.status().isOk()); // ok 응답이 왔는지 체크
    }

    // 2) 파라미터가 잘 들어왔는지 유효성 체크 ( implementation 'org.springframework.boot:spring-boot-starter-validation' 추가해서 검증 애노테이션을 적용한 것 )
    @DisplayName("신규 상품을 등록할 때 상품 타입은 필수값이다.")
    @Test
    public void createProductWithoutType() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                // .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400")) // MockMvc를 이용하면 Json 형식의 API Response를 검증할 수 있다. 이 때 사용되는 것이 JsonPath() 이다. (참고. $ : 전달받은 json 객체의 root element )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 타입은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 상품을 등록할 때 상품 판매상태는 필수값이다.")
    @Test
    public void createProductWithoutSellingStatus() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                // .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 판매상태는 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 상품을 등록할 때 상품 이름은 필수값이다.")
    @Test
    public void createProductWithoutName() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                // .name("아메리카노")
                .price(4000)
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 이름은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 상품을 등록할 때 상품 가격은 양수이다.")
    @Test
    public void createProductWithZeroPrice() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(0)
                .build();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 가격은 양수여야 합니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("판매 상품을 조회한다.")
    @Test
    public void getSellingProducts() throws Exception {

        // (어떤 상품이 저장되어 있는 상황에서) 정확한 데이터를 가져오는지에 대한 테스트는, 서비스 계층에서 테스트가 이루어 졌으므로, 컨트롤러 계층에서는 조회 요청시 Array 형태로 잘 오는가에 대한것만 검증하면 된다.

        // given
        List<ProductResponse> result = List.of();
        Mockito.when(productService.getSellingProducts()).thenReturn(List.of());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/selling")
                        // .queryParam("name", "이름")
                        // .queryParam("name", "이름")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray()); // Java의 List 타입은 JSON으로 직렬화될 때 배열(Array) 형태로 변환되기 때문에 isArray()로 확인하면 된다. ( https://www.inflearn.com/questions/1105245/data%EA%B0%80-isarray-ture%EC%9D%B8-%EC%9D%B4%EC%9C%A0 )
    }

}

// [ 참고 ]
// 위 예시에서 @MockBean private ProductService productService; 을 주석 처리 하고 @WebMvcTest 돌리면, ProductService 를 못찾는다고 나올것이다.
// 왜냐하면, ProductController 는 ProductService 가 있어야 만들어질 수 있는 빈이다. 따라서 ProductController 를 생성하는 과정에서 ProductService 가 없는 것이다.
// 따라서 ProductService 를 MockBean 처리를 해야 예외가 발생하지 않는다.