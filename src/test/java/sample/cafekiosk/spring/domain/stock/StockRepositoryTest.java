package sample.cafekiosk.spring.domain.stock;

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
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 참고) IntegrationTestSupport 로 리포지토리 계층도 통합테스트로 했다.
// : 박우빈 님 - @DataJpaTest 는 JPA 관련 빈들만 올려서 빠르게 테스트할 수 있다. 그런데 @DataJpaTest 를 사용하는 것이 특별한 장점이 있지않다면, 서비스 테스트(@SpringBootTest를 사용한 통합테스트)하면서 리포지토리 테스트도 같이 하는게 더 낫다고 판단한다. ( @DataJpaTest 를 위해 서버가 굳이 또 뜨는 시간이 아까움 )
// : 물론 꼭 이렇게 할 필요는 없다. @DataJpaTest 가 쓰고싶다면, 리포지토리 계층을 위한 테스트 상위 서포트 클래스를 만들어서 상속받아서 테스트해도된다.
@Transactional

// @ActiveProfiles("test")
// @DataJpaTest
// @Import(JpaAuditingConfig.class)
class StockRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("상품번호 리스트로 재고를 조회한다.")
    @Test
    public void findAllByProductNumberIn() throws Exception {
        // given
        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 3);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(
                List.of("001", "002"));

        // then
        Assertions.assertThat(stocks)
                .hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 1),
                        Tuple.tuple("002", 2)
                );
    }
}