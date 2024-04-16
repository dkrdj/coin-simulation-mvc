package com.mvc.coinsimulation.security;

import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsCustomService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long providerId = Long.parseLong(username);
        User user = userRepository.findByProviderId(providerId).orElseThrow(NoUserException::new);
        return new UserDetailsCustom(user.toDto());
    }
}
