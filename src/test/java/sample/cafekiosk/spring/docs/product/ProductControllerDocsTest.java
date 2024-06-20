package sample.cafekiosk.spring.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.docs.RestDocsSupport;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = Mockito.mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("신규 상품을 등록하는 API")
    @Test
    public void createProduct() throws Exception { // 테스트를 수행하면 테스트에 대한 문서 조각들이 생성된다 (build/generated-snippets/product-create/**.adoc)
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        // mocking 한 것에 대한 stubbing
        BDDMockito.given(productService.createProduct(any(ProductCreateServiceRequest.class)))
                .willReturn(
                        ProductResponse.builder()
                                .id(1L)
                                .productNumber("001")
                                .type(ProductType.HANDMADE)
                                .sellingStatus(ProductSellingStatus.SELLING)
                                .name("아메리카노")
                                .price(4000)
                                .build()
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                // 문서를 만들기 위한 chaining
                .andDo(document(
                        "product-create",
                        preprocessRequest(prettyPrint()), // 프로세싱 전 어떤 작업을 해줄지 지정할 수 있다. ( prettyPrint: json 이 한줄로 나오지 않고 예쁜 형태로 나옴 )
                        preprocessResponse(prettyPrint()),
                        requestFields( // 어떤게 요청되는지 명시 (field by field)
                                fieldWithPath("type").type(JsonFieldType.STRING).description("상품 타입"), // 하나 하나의 field 를 fieldWithPath(필드명, 필드타입(어떤 타입인지 명시), 설명) 라는 것으로 넣어준다.
                                fieldWithPath("sellingStatus").type(JsonFieldType.STRING).optional().description("상품 판매상태"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 가격")
                        ),
                        responseFields( // 어떤 응답이 내려오는지 명시 (field by field)
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                // data 하위에 들어가는 정보 명시
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.productNumber").type(JsonFieldType.STRING).description("상품 번호"),
                                fieldWithPath("data.type").type(JsonFieldType.STRING).description("상품 타입"),
                                fieldWithPath("data.sellingStatus").type(JsonFieldType.STRING).description("상품 판매상태"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품 이름"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                )); // document("해당 테스트에 대한 임의의 ID값", snippet) // snippet: (요청했을 때) 어떤 요청을 넣고 어떤 응답을 넣을 것 인가를 정의
    }
}

// 참고) fieldWithPath: 필드타입 에서 enum 은 STRING 에 해당한다.

// =====================================================================================================

// 참고) (요청/응답) 으로 만들어지는 테이블 재정의: request-fields.snippet, response.fields.snippets
//     ㄴ 필수값이 아닌 옵셔널 한 것이 있을 수 있으니, 해당 하는 정보도 추가
//     ㄴ 참고) 해당 snippet 파일은 /resources/org/springframework/restdocs/templates 에 두면 자동으로 인지된다.

// =====================================================================================================

// 참고) 테스트와 docs 를 위한 테스트를 나눠야할까..
//  ㄴ https://www.inflearn.com/questions/865256/spring-rest-docs-%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8

// 참고) jar 파일에 생성될 static/docs 는 개발환경에서는 쓰는데, 운영에서 접근하면 안되지 않나?
//  ㄴ https://www.inflearn.com/questions/1140077/spring-rest-docs-%EA%B0%95%EC%9D%98-%EC%A7%88%EB%AC%B8%EC%9D%B4-%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4

// ====================================================================================================

// 참고) 전체 과정
// Rest Docs 문서(html) 생성은 크게 세 가지 단계로 나누어 수행한다.
// 1) 테스트 코드 작성/실행 -> adoc 문서 생성
//  - mockMvc 에 대해서 RestDocumentation 에 대한 설정
//  - 이후 테스트 작성하고 수행하면, 테스트 수행에 대한 결과물로, 문서 조각들이 지정된 경로에 생성된다 ( build/generated-snippets/product-create/**.adoc )
// 2) adoc 문서 조합 -> 완성된 문서를 위한 adoc 문서 생성 ( 코드 조각들을 하나로 합쳐서 문서로 만든다. )
//  - src/docs/asciidoc 경로 하위에 adoc 파일을 작성한다.
//    - adoc 파일 내부에는 문서에 포함할 snippets 등을 지정
// 3) adoc 문서를 html 문서로 변환
//  - Task > documentation > asciidoctor 실행
//  - build/docs/asciidoc 에 html 파일이 생성됨
// - 참고) https://ppaksang.tistory.com/39