package sample.cafekiosk.spring.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    HANDMADE("제조 음료"),
    BOTTLE("병 음료"),
    BAKERY("베이커리");

    private final String text;

    // 재고가 있는 유형의 상품인지 체크
    public static boolean containsStockType(ProductType type) {
        return List.of(BOTTLE, BAKERY).contains(type);
    }
}
