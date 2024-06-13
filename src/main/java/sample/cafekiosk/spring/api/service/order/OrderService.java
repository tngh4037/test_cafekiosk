package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);

        // 재고 차감 대상이 되는 상품을 필터링
        List<String> stockProductNumbers = products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(product -> product.getProductNumber())
                .collect(Collectors.toList());

        // 재고 엔티티 조회
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        Map<String, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(stock -> stock.getProductNumber(), s -> s));

        // 상품별 카운팅
        Map<String, Long> productCountingMap = stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        // 재고 차감 시도
        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if (stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(quantity);
        }

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.of(savedOrder);
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

        // [ 중복되는 상품번호 리스트로 주문을 생성할 수 있다. ]
        // 1) 사용자가 주문한 상품을 조회해서, 상품 번호를 기반으로 (상품을 찾을 수 있도록) map 생성
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(product -> product.getProductNumber(), p -> p));

        // 2) 사용자가 주문한 상품 번호를 순회하면서, map 에서 해당 상품 엔티티 조회
        List<Product> duplicateProducts = productNumbers.stream()
                .map(productNumber -> productMap.get(productNumber))
                .collect(Collectors.toList());

        return duplicateProducts;
    }
}
