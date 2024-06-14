package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * readOnly = true: 읽기전용
 * - CRUD 에서 CUD 작업 동작 X  /  only Read 만 가능
 * - JPA 에서는 스냅샷 저장, 변경감지를 하지 않음 (성능 향상)
 *
 * 참고)
 * - CQRS - Command(CUD 행위) 와 Query(Read 행위) 를 분리하자. (책임을 분리하자.) -> 서로 연관이 없게끔하자.
 * - 이 CQRS 에 대한 시작이 될 수 있는 것이, @Transactional(readOnly = true) 를 신경써서 작성하는 것이다.
 * - 코드레벨 에서는 클래스 레벨에 @Transactional(readOnly = true) 를 설정하고, CUD 작업이 발생하는 곳에는 @Transactional 를 설정하자.
 * - 더 나아가, 각 서비스를 분리하는 것도 좋다.
 *
 * 참고) @Transactional readOnly 옵션 값으로 DB endpoint 를 구분할 수 있다.
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(product -> ProductResponse.of(product))
                .collect(Collectors.toList());
    }

    // productNumber 부여 ( DB 에서 마지막 저장된 Product 의 상품 번호를 읽어와서 +1 )
    private String createNextProductNumber() {
        String latestProductNumber = productRepository.findLatestProductNumber();
        if (latestProductNumber == null) {
            return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumberInt = latestProductNumberInt + 1;

        // 9 -> 009 , 10 -> 010
        return String.format("%03d", nextProductNumberInt);
    }
}
