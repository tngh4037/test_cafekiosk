package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

}