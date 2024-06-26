package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.request.UserInfoChangeRequest;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.exception.NotEnoughCashException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import com.mvc.coinsimulation.util.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Utils s3Utils;

    private User getUserForUpdate(Long userId) {
        return userRepository.findByIdForUpdate(userId).orElseThrow(NoUserException::new);
    }

    @Transactional
    public void updateUserCash(User user, BigDecimal cash) {
        user.setCash(user.getCash().add(cash));
    }

    @Transactional
    public void updateUserCash(Order order) {
        User user = order.getUser();
        user.setCash(user.getCash().add(order.getAmount().multiply(order.getPrice())));
    }

    @Transactional
    public void updateUserCash(Long userId, OrderRequest orderRequest) {
        User user = getUserForUpdate(userId);
        BigDecimal totalPrice = orderRequest.getPrice().multiply(orderRequest.getAmount());
        if (user.getCash().compareTo(totalPrice) >= 0) {
            user.setCash(user.getCash().subtract(totalPrice));
        } else {
            throw new NotEnoughCashException();
        }
    }

    /**
     * 특정 사용자의 정보를 조회하는 메서드
     *
     * @param userId 사용자의 ID
     * @return UserResponse
     */
    public UserResponse getUserInfo(Long userId) {
        return userRepository.findById(userId).orElseThrow(NoUserException::new).toResponse();
    }

    /**
     * 사용자 정보를 변경하는 메서드
     *
     * @param userId  사용자의 ID
     * @param request 변경할 사용자 정보 요청 객체
     * @return UserResponse
     */
    @Transactional
    public UserResponse changeUserInfo(Long userId, UserInfoChangeRequest request) {
        User user = getUserForUpdate(userId);
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        return user.toResponse();
    }

    /**
     * 사용자 프로필 사진을 변경하는 메서드
     *
     * @param userId 사용자의 ID
     * @param file   변경할 프로필 사진 파일
     * @return UserResponse
     * @throws IOException 파일 업로드 중 발생한 예외
     */
    @Transactional
    public UserResponse changeUserProfile(Long userId, MultipartFile file) throws IOException {
        User user = getUserForUpdate(userId);
        user.setProfile(s3Utils.uploadFromFile(file, userId));
        return user.toResponse();
    }

    /**
     * 상위 10명의 사용자 정보를 조회하는 메서드
     *
     * @return List<UserResponse>
     */
    public List<UserResponse> getTop10Users() {
        return userRepository.findTop10ByOrderByCashDesc().stream()
                .map(User::toResponse)
                .collect(Collectors.toList());
    }
}
