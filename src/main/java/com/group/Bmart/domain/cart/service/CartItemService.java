package com.group.Bmart.domain.cart.service;

import com.group.Bmart.domain.cart.Cart;
import com.group.Bmart.domain.cart.CartItem;
import com.group.Bmart.domain.cart.exception.NotFoundCartException;
import com.group.Bmart.domain.cart.exception.NotFoundCartItemException;
import com.group.Bmart.domain.cart.repository.CartItemRepository;
import com.group.Bmart.domain.cart.repository.CartRepository;
import com.group.Bmart.domain.cart.service.request.RegisterCartItemCommand;
import com.group.Bmart.domain.cart.service.request.UpdateCartItemCommand;
import com.group.Bmart.domain.cart.service.response.FindCartItemResponse;
import com.group.Bmart.domain.cart.service.response.FindCartItemsResponse;
import com.group.Bmart.domain.item.Item;
import com.group.Bmart.domain.item.exception.NotFoundItemException;
import com.group.Bmart.domain.item.repository.ItemRepository;
import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long registerCartItem(
            RegisterCartItemCommand registerCartItemCommand
    ) {
        User foundUser = userRepository.findById(registerCartItemCommand.userId())
                .orElseThrow(() -> new NotFoundUserException("존재하지 않은 사용자입니다."));

        Cart foundCart = cartRepository.findByUser(foundUser)
                .orElseGet(() -> {
                            Cart savedCart = cartRepository.save(new Cart(foundUser));
                            return savedCart;
                        }
                );

        Item foundItem = itemRepository.findById(registerCartItemCommand.itemId())
                .orElseThrow(() -> new NotFoundItemException("존재하지 않은 상품입니다."));

        CartItem cartItem = CartItem.builder()
                .cart(foundCart)
                .item(foundItem)
                .quantity(registerCartItemCommand.quantity())
                .build();

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return savedCartItem.getCartItemId();
    }

    @Transactional
    public void deleteCartItem(
            final Long cartItemId
    ) {
        CartItem foundCartItem = findCartItemByCartItemId(cartItemId);

        cartItemRepository.delete(foundCartItem);
    }

    @Transactional(readOnly = true)
    public FindCartItemsResponse findCartItemsByUserId(
            final Long userId
    ) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));

        Cart foundCart = cartRepository.findByUser(foundUser)
                .orElseThrow(() -> new NotFoundCartException("해당 장바구니를 찾을 수 없습니다."));

        List<CartItem> foundCartItems = cartItemRepository.findAllByCartOrderByCreatedAt(
                foundCart);

        return FindCartItemsResponse.from(foundCartItems
                .stream()
                .map(
                        cartItem -> FindCartItemResponse.of(
                                cartItem.getCart().getCartId(),
                                cartItem.getItem().getItemId(),
                                cartItem.getQuantity()
                        )
                )
                .toList());
    }

    @Transactional(readOnly = true)
    public int getCartItemTotalPrice(
            final Long cartItemId
    ) {
        int totalPrice = 0;

        List<CartItem> cartItems = cartItemRepository.findAllByCartItemIdOrderByCreatedAt(
                cartItemId);

        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getItem().getPrice() * cartItem.getQuantity();
        }

        return totalPrice;
    }

    @Transactional
    public void updateCartItemQuantity(UpdateCartItemCommand updateCartItemCommand) {
        CartItem foundCartItem = findCartItemByCartItemId(updateCartItemCommand.cartId());

        foundCartItem.changeQuantity(updateCartItemCommand.quantity());
    }

    private CartItem findCartItemByCartItemId(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(
                        () -> new NotFoundCartItemException("장바구니 상품이 존재하지 않습니다.")
                );
    }
}
