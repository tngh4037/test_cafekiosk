package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
    @Test
    public void isQuantityLessThan() throws Exception {
        // given
        Stock stock = Stock.create("001", 1);
        int requestQuantity = 2;

        // when
        boolean result = stock.isQuantityLessThan(requestQuantity);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("재고를 주어진 개수만큼 차감할 수 있다.")
    @Test
    public void deductQuantity() throws Exception {
        // given
        Stock stock = Stock.create("001", 1);
        int requestQuantity = 1;

        // when
        stock.deductQuantity(requestQuantity);

        // then
        assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("재고보다 많은 수의 수량으로 차감 시도하는 경우 예외가 발생한다.")
    @Test
    public void deductQuantity2() throws Exception {
        // given
        Stock stock = Stock.create("001", 1);
        int requestQuantity = 2;

        // when & then
        assertThatThrownBy(() -> stock.deductQuantity(requestQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");
    }

    // dynamicTest 형태)
    @DisplayName("")
    @TestFactory // @Test 대신 @TestFactory 를 선언한다.
    Collection<DynamicTest> dynamicTest() { // 리턴 값으로 Collection, Stream 등 Iterable 한 것들을 던지면 된다.
        // given
        // 공유 환경 구성

        return List.of( // List 형태로 DynamicTest 를 단계별로 던지면서, 단계별로 일련의 시나리오를 수행하고 싶은 경우 ( 단계별로 행위와 검증을 수행하고 싶은 경우 )
                DynamicTest.dynamicTest("", () -> {

                }),
                DynamicTest.dynamicTest("", () -> {

                })
        );
    }

    // dynamicTest 예시)
    @DisplayName("재고 차감 시나리오")
    @TestFactory
    Collection<DynamicTest> stockDeductionDynamicTest() {
        // given
        Stock stock = Stock.create("001", 1);

        return List.of(
                DynamicTest.dynamicTest("재고를 주어진 개수만큼 차감할 수 있다.", () -> {
                    // given
                    int quantity = 1;

                    // when
                    stock.deductQuantity(quantity);

                    // then
                    assertThat(stock.getQuantity()).isZero();
                }),
                DynamicTest.dynamicTest("재고보다 많은 수의 수량으로 차감 시도하는 경우 예외가 발생한다.", () -> {
                    // given
                    int quantity = 1;

                    // when & then
                    assertThatThrownBy(() -> stock.deductQuantity(quantity))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("차감할 재고 수량이 없습니다.");
                })
        );
    }
}