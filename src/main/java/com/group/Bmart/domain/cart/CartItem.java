package com.group.Bmart.domain.cart;

import com.group.Bmart.domain.cart.exception.InvalidCartItemException;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Getter
@Entity
@Table (name = "cart_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    private static final int MIN_QUANTITY = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean isChecked;

    @Builder
    public CartItem(
            final Cart cart,
            final Item item,
            final int quantity
    ) {
        validateCart(cart);
        validateItem(item);
        validateQuantity(quantity);
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
        this.isChecked = true;
    }

    public void validateCart(final Cart cart) {
        if (isNull(cart)) {
            throw new InvalidCartItemException("Cart 가 존재하지 않습니다.");
        }
    }

    public void validateItem(final Item item) {
        if (isNull(item)) {
            throw new InvalidCartItemException("Item 이 존재하지 않습니다.");
        }
    }

    public void validateQuantity(final int quantity) {
        if (quantity < MIN_QUANTITY) {
            throw new InvalidCartItemException("수량은 음수가 될 수 없습니다.");
        }
    }

    public void changeQuantity(final int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }
}