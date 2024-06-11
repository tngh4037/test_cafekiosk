package sample.cafekiosk.unit;

import lombok.Getter;
import sample.cafekiosk.unit.beverage.Beverage;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * [ 요구사항 ]
 * - 주문 목록에 음료 추가 / 삭제 기능
 * - 주문 목록 전체 지우기
 * - 주문 목록 총 금액 계산하기
 * - 주문 생성하기
 */
@Getter
public class CafeKiosk {

    private final List<Beverage> beverages = new ArrayList<>();

    public void add(Beverage beverage) {
        beverages.add(beverage);
    }

    // 요구사항 추가: 한 종류의 음료 여러 잔을 한 번에 담을 수 있어야 한다.
    public void add(Beverage beverage, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("음료는 1잔 이상 주문하실 수 있습니다.");
        }
        for (int i = 0; i < count; i++) {
            beverages.add(beverage);
        }
    }

    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }

    public void clear() {
        beverages.clear();
    }

    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Beverage beverage : beverages) {
            totalPrice += beverage.getPrice();
        }
        return totalPrice;
    }

    public Order createOrder() {
        return new Order(LocalDateTime.now(), beverages);
    }
    
}
