package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
    @Test
    public void calculateTotalPrice() throws Exception {
        // given
        List<Product> products = List.of(
                createProduct("001", 1000, "아메리카노"),
                createProduct("002", 2000, "카페라떼")
        );

        // when
        Order order = Order.create(products, LocalDateTime.now());

        // then
        assertThat(order.getTotalPrice()).isEqualTo(3000);
    }

    @DisplayName("주문 생성 시 주문 상태는 INIT 이다.")
    @Test
    public void init() throws Exception {
        // given
        List<Product> products = List.of(
                createProduct("001", 1000, "아메리카노"),
                createProduct("002", 2000, "카페라떼")
        );

        // when
        Order order = Order.create(products, LocalDateTime.now());

        // then
        assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.INIT); // isEqualByComparingTo : enum 값은 그 자체로 비교해준다.
    }

    @DisplayName("주문 생성 시 주문 등록 시간을 기록한다.")
    @Test
    public void registeredDateTime() throws Exception {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        List<Product> products = List.of(
                createProduct("001", 1000, "아메리카노"),
                createProduct("002", 2000, "카페라떼")
        );

        // when
        Order order = Order.create(products, registeredDateTime);

        // then
        assertThat(order.getRegisteredDateTime()).isEqualTo(registeredDateTime);
    }

    private Product createProduct(String productNumber, int price, String name) {
        return Product.builder()
                .type(ProductType.HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name(name)
                .build();
    }

}