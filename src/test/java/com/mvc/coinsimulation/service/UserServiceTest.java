package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import com.mvc.coinsimulation.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    S3Util s3Util;

    UserService userService;

    @BeforeEach
    void beforeEach() {
        userService = new UserService(userRepository, s3Util);
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .nickname("test1")
                .role("USER")
                .profile("profile")
                .providerId(1L)
                .cash(20000000d)
                .build();
        Optional<User> optionalUser = Optional.of(user);
        lenient().when(userRepository.findByIdForUpdate(1L)).thenReturn(optionalUser);
        lenient().when(userRepository.findById(1L)).thenReturn(optionalUser);

        lenient().when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());
        lenient().when(userRepository.findById(2L)).thenReturn(Optional.empty());
    }

    @Test
    void updateUserCash_order_noUser() {
        //given
        Order order = Order.builder()
                .userId(2L)
                .build();

        //when then
        assertThrows(NoUserException.class, () -> userService.updateUserCash(order));
    }

    @Test
    void updateUserCash_order_checkCash() {
        //given
        Order order = Order.builder()
                .userId(1L)
                .amount(1d)
                .price(100000d)
                .build();
        User user = userRepository.findById(1L).get();

        //when
        userService.updateUserCash(order);

        //then
        assertEquals(20000000d + 1d * 100000d, user.getCash());
    }

    @Test
    void testUpdateUserCash() {
    }

    @Test
    void getUserInfo() {
    }

    @Test
    void changeUserInfo() {

    }

    @Test
    void changeUserProfile() throws IOException {
//        when(s3Util.uploadFromFile(null, 1L)).thenReturn("newProfile");
    }

    @Test
    void getTop10Users() {
        //given
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long tmpUserId = (long) (10 + i);
            User tmpUser = User.builder()
                    .id(tmpUserId)
                    .nickname("test" + tmpUserId)
                    .role("USER")
                    .profile("profile")
                    .providerId(tmpUserId)
                    .cash(20000000d - i * 1000000)
                    .build();
            userList.add(tmpUser);
        }
        when(userRepository.findTop10ByOrderByCashDesc()).thenReturn(userList);

        //when
        List<UserResponse> list = userService.getTop10Users();

        //then
        for (int i = 0; i < 10; i++) {
            Long tmpUserId = (long) (10 + i);
            UserResponse userResponse = list.get(i);
            assertEquals("test" + tmpUserId, userResponse.getNickname());
            assertEquals("profile", userResponse.getProfile());
            assertEquals(20000000d - i * 1000000, userResponse.getCash());
        }
    }
}