package sample.cafekiosk.unit.beverage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {
    
    @Test
    void getName() {
        Americano americano = new Americano();
        assertEquals(americano.getName(), "아메리카노"); // JUnit 을 사용한 검증
        assertThat(americano.getName()).isEqualTo("아메리카노"); // AssertJ 를 사용한 검증
    }

    @Test
    void getPrice() {
        Americano americano = new Americano();
        assertThat(americano.getPrice()).isEqualTo(4000);
    }

}