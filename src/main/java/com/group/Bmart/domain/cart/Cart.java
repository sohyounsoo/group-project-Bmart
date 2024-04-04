package com.group.Bmart.domain.cart;


import com.group.Bmart.domain.user.User;
import com.group.Bmart.domain.user.exception.NotFoundUserException;
import com.group.Bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static java.util.Objects.isNull;

@Getter
@Entity
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Cart(
            final User user
    ) {
        validateUser(user);
        this.user = user;
    }

    public void validateUser(
            final User user
    ) {
        if (isNull(user)) {
            throw new NotFoundUserException("User 가 존재하지 않습니다.");
        }
    }
}
