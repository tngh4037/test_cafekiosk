package sample.cafekiosk.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing // https://januaryman.tistory.com/519 ( @EnableJpaAuditing과 @SpringBootApplication 을 같이 쓸때, Test에서 발생되는 에러 )
@SpringBootApplication
public class CafekioskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafekioskApplication.class, args);
	}

}
