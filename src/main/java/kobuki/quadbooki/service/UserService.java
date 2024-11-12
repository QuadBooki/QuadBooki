package kobuki.quadbooki.service;

import kobuki.quadbooki.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 회원가입
    /*public void registerUser(User user) {
        validateUser(user);
        userRepository.save(user);
    }*/

}
