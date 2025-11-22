package com.backthree.cohobby.domain.user.service;

import com.backthree.cohobby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        //여기서 username: 식별자인 email
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + username));
    }
}
