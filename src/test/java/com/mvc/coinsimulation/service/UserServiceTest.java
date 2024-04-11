package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.request.UserInfoChangeRequest;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import com.mvc.coinsimulation.util.S3Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Util s3Util;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .nickname("test1")
                .role("USER")
                .profile("profile1")
                .providerId(1L)
                .cash(20000000d)
                .build();
        Optional<User> optionalUser = Optional.of(user);
        lenient().when(userRepository.findByIdForUpdate(1L)).thenReturn(optionalUser);

        lenient().when(userRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("매도 주문 체결로 인한 유저 현금 증가 테스트")
    void updateUserCash_order() {
        //given
        Order order1 = Order.builder()
                .userId(1L)
                .amount(1d)
                .price(100000d)
                .build();
        Order order2 = Order.builder()
                .userId(2L)
                .build();
        User user = userRepository.findByIdForUpdate(1L).get();

        //when
        userService.updateUserCash(order1);

        //then
        assertThrows(NoUserException.class, () -> userService.updateUserCash(order2));
        assertEquals(20100000d, user.getCash());
    }

    @Test
    @DisplayName("매수 주문 신청으로 인한 유저 현금 감소 테스트")
    void updateUserCash_orderRequest() {
        //given
        OrderRequest orderRequest = new OrderRequest("KRW-BTC", 2000000d, 1.2d);
        User user = userRepository.findByIdForUpdate(1L).get();

        //when
        userService.updateUserCash(1L, orderRequest);

        //then
        assertEquals(17600000d, user.getCash());
        assertThrows(NoUserException.class, () -> userService.updateUserCash(2L, orderRequest));
    }

    @Test
    @DisplayName("유저 정보 조회 테스트")
    void getUserInfo() {
        //given
        //@BeforeEach에 기술

        //when
        UserResponse userResponse = userService.getUserInfo(1L);

        //then
        assertThrows(NoUserException.class, () -> userService.getUserInfo(2L));
        assertEquals(userResponse.getNickname(), "test1");
        assertEquals(userResponse.getProfile(), "profile1");
        assertEquals(userResponse.getCash(), 20000000d);
    }

    @Test
    @DisplayName("유저 정보 변경 테스트")
    void changeUserInfo() {
        //given
        String changedNickname = "changedNickname";
        UserInfoChangeRequest userInfoChangeRequest_1 = new UserInfoChangeRequest();
        UserInfoChangeRequest userInfoChangeRequest_2 = new UserInfoChangeRequest(changedNickname);

        User user = userRepository.findByIdForUpdate(1L).get();

        //when
        UserResponse userResponse_1 = userService.changeUserInfo(1L, userInfoChangeRequest_1);

        //then
        assertEquals(user.getNickname(), userResponse_1.getNickname());
        assertEquals(user.getCash(), userResponse_1.getCash());
        assertEquals(user.getProfile(), userResponse_1.getProfile());

        //when
        UserResponse userResponse_2 = userService.changeUserInfo(1L, userInfoChangeRequest_2);

        //then
        assertEquals(changedNickname, userResponse_2.getNickname());
        assertEquals(user.getCash(), userResponse_2.getCash());
        assertEquals(user.getProfile(), userResponse_2.getProfile());

        assertThrows(NoUserException.class, () -> userService.changeUserInfo(2L, userInfoChangeRequest_1));
        assertThrows(NoUserException.class, () -> userService.changeUserInfo(2L, userInfoChangeRequest_2));
    }

    @Test
    @DisplayName("유저 프로필 사진 변경 테스트")
    void changeUserProfile() throws IOException {
        //given
        String newProfile = "newProfile";
        when(s3Util.uploadFromFile(any(), anyLong())).thenReturn(newProfile);

        //when
        UserResponse userResponse = userService.changeUserProfile(1L, null);

        //then
        assertEquals(newProfile, userResponse.getProfile());
        assertThrows(NoUserException.class, () -> userService.changeUserProfile(2L, null));

    }

    @Test
    @DisplayName("랭킹 조회 테스트")
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
