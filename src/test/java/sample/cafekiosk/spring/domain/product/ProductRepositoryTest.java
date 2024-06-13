package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // test 프로파일로 설정값이 적욛되도록 설정
// @SpringBootTest // 스프링에서 통합 테스트를 위해 제공하는 애노테이션 ( 테스트를 실행할 때, 스프링 서버를 띄워서 테스트할 수 있게된다. )
@DataJpaTest // 스프링 서버를 띄워서 테스트 할 수 있다. 단, @DataJpaTest 는 @SpringBootTest 보다는 가볍다. ( JPA 관련된 빈들만 주입을 해줘서 서버를 띄워준다. 따라서 @SpringBootTest 에 비해서는 속도가 빠르다. ) => 그런데 박우빈님은 @DataJpaTest 보다는 @SpringBootTest 를 더 선호한다고 한다.
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    public void findAllBySellingStatusIn() throws Exception {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.HOLD)
                .name("카페라떼")
                .price(4500)
                .build();
        Product product3 = Product.builder()
                .productNumber("003")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
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
        Product product1 = Product.builder()
                .productNumber("001")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.HOLD)
                .name("카페라떼")
                .price(4500)
                .build();
        Product product3 = Product.builder()
                .productNumber("003")
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
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

}