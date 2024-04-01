package com.mvc.coinsimulation.service;

import com.mvc.coinsimulation.dto.request.OrderRequest;
import com.mvc.coinsimulation.dto.request.UserInfoChangeRequest;
import com.mvc.coinsimulation.dto.response.UserResponse;
import com.mvc.coinsimulation.entity.Order;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.exception.NotEnoughCashException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import com.mvc.coinsimulation.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final S3Util s3Util;

    @Transactional
    public void updateUserCash(Order order) {
        User user = userRepository.findByIdForUpdate(order.getUserId()).orElseThrow(NoUserException::new);
        user.setCash(user.getCash() + order.getAmount() * order.getPrice());
    }

    @Transactional
    public void updateUserCash(Long userId, OrderRequest orderRequest) {
        User user = userRepository.findByIdForUpdate(userId).orElseThrow(NoUserException::new);
        Double totalPrice = orderRequest.getPrice() * orderRequest.getAmount();
        if (user.getCash() >= totalPrice) {
            user.setCash(user.getCash() - totalPrice);
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
        return userRepository.findById(userId).orElseThrow().toResponse();
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
        User user = userRepository.findById(userId).orElseThrow();
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
        User user = userRepository.findById(userId).orElseThrow();
        user.setProfile(s3Util.uploadFromFile(file, userId));
        return user.toResponse();
    }

    /**
     * 상위 10명의 사용자 정보를 조회하는 메서드
     *
     * @return List<UserResponse>
     */
    public List<UserResponse> getTop10Users() {
        return userRepository.findTop10ByOrderByCashDesc().stream()
                .map(user -> user.toResponse())
                .collect(Collectors.toList());
    }
}
