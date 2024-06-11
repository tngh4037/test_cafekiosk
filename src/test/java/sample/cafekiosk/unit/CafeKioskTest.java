package sample.cafekiosk.unit;

import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CafeKioskTest {

    @Test
    void add_manual_test() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수: " + cafeKiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료: " + cafeKiosk.getBeverages().get(0).getName());
    }

    @Test
    void add() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        assertThat(cafeKiosk.getBeverages().size()).isEqualTo(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노"); // 최종 단계에서 사람이 확인하지 X, 작성된 검증이 통과하는지 안통과하는지만 체크 ( 이후에 Cafekiosk 의 로직이 변경되더라도, 테스트 코드를 수행해봄으로써 (사람이 개입하지 않더라도) 프로덕션 코드가 정상 동작하는지를 체크할 수 있다. )
    }

    @Test
    void addSeveralBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano, 2);

        // 해피 케이스
        assertThat(cafeKiosk.getBeverages().get(0)).isEqualTo(americano);
        assertThat(cafeKiosk.getBeverages().get(1)).isEqualTo(americano);
    }

    @Test
    void addZeroBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        // 예외 케이스
        assertThatThrownBy(() -> cafeKiosk.add(americano, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문하실 수 있습니다."); // 어떤 상황이 주어졌을때 어떤 예외가 발생해야 하는지 검증 ( + 어떤 메시지를 던지는 예외인지도 체크 가능 )
    }

    @Test
    void remove() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);
        assertThat(cafeKiosk.getBeverages()).hasSize(1);

        cafeKiosk.remove(americano);
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    void clear() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafeKiosk.add(americano);
        cafeKiosk.add(latte);
        assertThat(cafeKiosk.getBeverages()).hasSize(2);

        cafeKiosk.clear();
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

}

// [ (사람이 최종적으로 확인해야 하는) 수동 테스트의 문제점 ]
// 1) 테스트를 콘솔에 찍어서 콘솔 결과를 기준으로, 테스트의 정상 동작여부를 사람이 확인했다. ( 최종 단계에서 사람이 개입 )
// 2) 다른 사람이 이 테스트 코드를 봤을 때, 뭘 검증해야 하는지, 어떤게 틀린 상황이고 어떤게 맞는 상황인지 알수없다.
//
// 따라서, 자동화된 테스트를 해야하며, 자동화 테스트 도구로 JUnit 을 사용해서 검증할 수 있다.
//
// 참고) JUnit5
// : 단위 테스트를 위한 테스트 프레임워크
//