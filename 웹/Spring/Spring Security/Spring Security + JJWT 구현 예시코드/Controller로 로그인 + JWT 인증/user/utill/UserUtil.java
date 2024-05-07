package com.e102.simcheonge_server.domain.user.utill;

import com.e102.simcheonge_server.domain.user.entity.User;
import com.e102.simcheonge_server.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserUtil {

    private static UserRepository userRepository;

    @Autowired
    public UserUtil(UserRepository userRepository) {
        UserUtil.userRepository = userRepository;
    }

    public static User getUserFromUserDetails(UserDetails userDetails) {
        String userLoginId = userDetails.getUsername();
        return userRepository.findByUserLoginId(userLoginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userLoginId));
    }
}
