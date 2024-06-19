package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.config.JpaAuditingConfig;

import java.util.List;

// 참고) IntegrationTestSupport 로 리포지토리 계층도 통합테스트로 했다.
// : 박우빈 님 - @DataJpaTest 는 JPA 관련 빈들만 올려서 빠르게 테스트할 수 있다. 그런데 @DataJpaTest 를 사용하는 것이 특별한 장점이 있지않다면, 서비스 테스트(@SpringBootTest를 사용한 통합테스트)하면서 리포지토리 테스트도 같이 하는게 더 낫다고 판단한다. ( @DataJpaTest 를 위해 서버가 굳이 또 뜨는 시간이 아까움 )
// : 물론 꼭 이렇게 할 필요는 없다. @DataJpaTest 가 쓰고싶다면, 리포지토리 계층을 위한 테스트 상위 서포트 클래스를 만들어서 상속받아서 테스트해도된다.
// : 여기서는 박우빈님은 환경 통합시에는 IntegrationTestSupport 를 사용했다. (따라서 @Transactional 도 추가)
@Transactional

// @ActiveProfiles("test") // test 프로파일로 설정값이 적욛되도록 설정
// // @SpringBootTest // 스프링에서 통합 테스트를 위해 제공하는 애노테이션 ( 테스트를 실행할 때, 스프링 서버를 띄워서 테스트할 수 있게된다. )
// @DataJpaTest // 스프링 서버를 띄워서 테스트 할 수 있다. 단, @DataJpaTest 는 @SpringBootTest 보다는 가볍다. ( JPA 관련된 빈들만 주입을 해줘서 서버를 띄워준다. 따라서 @SpringBootTest 에 비해서는 속도가 빠르다. ) => 그런데 박우빈님은 @DataJpaTest 보다는 @SpringBootTest 를 더 선호한다고 한다.
// @Import(JpaAuditingConfig.class)
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    public void findAllBySellingStatusIn() throws Exception {
        // given
        Product product1 = createProduct("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", ProductType.HANDMADE, ProductSellingStatus.HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", ProductType.HANDMADE, ProductSellingStatus.STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllBySellingStatusIn(
                List.of(ProductSellingStatus.SELLING, ProductSellingStatus.HOLD));

        // then
        Assertions.assertThat(products)
                .hasSize(2)
                .extracting("productNumber", "name", "sellingStatus") // 검증하고자 하는 필드만 추출
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", "아메리카노", ProductSellingStatus.SELLING),
                        Tuple.tuple("002", "카페라떼", ProductSellingStatus.HOLD)
                );
    }

    @DisplayName("상품번호 리스트로 상품들을 조회한다.")
    @Test
    public void findAllByProductNumberIn() throws Exception {
        // given
        Product product1 = createProduct("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", ProductType.HANDMADE, ProductSellingStatus.HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", ProductType.HANDMADE, ProductSellingStatus.STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllByProductNumberIn(
                List.of("001", "002"));

        // then
        Assertions.assertThat(products)
                .hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", "아메리카노", ProductSellingStatus.SELLING),
                        Tuple.tuple("002", "카페라떼", ProductSellingStatus.HOLD)
                );
    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    @Test
    public void findLatestProduct() throws Exception {
        // given
        String targetProductNumber = "003";
        Product product1 = createProduct("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", ProductType.HANDMADE, ProductSellingStatus.HOLD, "카페라떼", 4500);
        Product product3 = createProduct(targetProductNumber, ProductType.HANDMADE, ProductSellingStatus.STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        String latestProductNumber = productRepository.findLatestProductNumber();

        // then
        Assertions.assertThat(latestProductNumber).isEqualTo(targetProductNumber);
    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올 때, 상품이 하나도 없는 경우에는 NULL을 반환한다.")
    @Test
    public void findLatestProductNumberWhenProductIsEmpty() throws Exception {

        // when
        String latestProductNumber = productRepository.findLatestProductNumber();

        // then
        Assertions.assertThat(latestProductNumber).isNull();
    }

    private Product createProduct(String productNumber, ProductType type,
                                  ProductSellingStatus sellingStatus, String name, int price) {
        Product product1 = Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
        return product1;
    }

}