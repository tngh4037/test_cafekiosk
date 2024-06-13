package sample.cafekiosk.spring.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * ex>
     * select *
     * from product
     * where selling_status in ('SELLING', 'HOLD');
     */
    List<Product> findAllBySellingStatusIn(List<ProductSellingStatus> sellingStatuses); // 쿼리 메서드 기능

    /**
     * ex>
     * select *
     * from product
     * where product_number in ('001', '002');
     */
    List<Product> findAllByProductNumberIn(List<String> productNumbers);
}
