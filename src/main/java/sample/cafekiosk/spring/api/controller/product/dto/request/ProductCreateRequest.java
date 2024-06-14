package sample.cafekiosk.spring.api.controller.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "상품 타입은 필수입니다.") // 참고) Enum 은 Enum 자체 값이나 null 만 올수있기 때문에, 주로 @NotNull 로 검증한다.
    private ProductType type;

    @NotNull(message = "상품 판매상태는 필수입니다.")
    private ProductSellingStatus sellingStatus;

    @NotBlank(message = "상품 이름은 필수입니다.") // // null, "", "  " 모두 아니어야 한다. ( 참고로 String 은 주로 NotBlack 를 사용해서 검증한다. )
    // @NotNull // null이 아니어야 한다. ( "",  "  " 은 통과된다. )
    // @NotEmpty // null, "" 이 아니어야 한다. ( "  " 은 통과된다. )
    private String name;

    @Positive(message = "상품 가격은 양수여야 합니다.")
    private int price;

    @Builder
    private ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }
}

// 참고) Validation 에 대한 책임 분리
// 예를 들어, 상품 이름은 20자 제한을 두는 정책이 생겼다고 가정해보자.
// 그럼 위에서 @Max(20) 등으로 체크해서 여기서 검증하는게 좋을까? -> 상품 20자 제한이라는 정책을 과연 컨트롤러 레이어에서 튕겨낼 책임이 맞는가에 대한 고민을 해보는게 좋다.
//
// "@NotBlank 와 같은 기본적으로 유효한 문자열 이라면 합당히 가져야 할 속성들에 대한 Validation" 과
// "상품 이름 20자 제한 같은, 우리 도메인 성격에 핏한 특수한 성격의 Validation" 을 구분할 줄 아는 시야를 기르자.
//
// 박우빈님)
// - 나라면, 컨트롤러 단에서는 String 에 대한 최소한의 조건. 즉, NotBlank 만 테스트를 하고,
// - 20자 제한과 같은 검증은 조금 더 안쪽에서,
//   ㄴ 1) 서비스 계층으로 타고 들어가서 서비스 계층에서 검증을 하거나
//   ㄴ 2) 아니면 도메인 객체(Product)에서, Product 를 생성하는 시점에 생성자에서 검증을 하던
//   ㄴ 더 안쪽 레이어에서 검증을 하는게 더 좋다라고 생각한다.
// - 하나의 validation 으로 보이더라도, 성격에 따라서 어느 레이어에서 검증할지 구분하는 안목을 기르자. (한번에 한 레이어에서 검증할 필요는 없다.)
//