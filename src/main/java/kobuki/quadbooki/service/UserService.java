package kobuki.quadbooki.service;

import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    // 회원가입 처리
    public void register(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        userRepository.save(user);
    }

    // 비밀번호를 SHA-256으로 해시 처리
    private String hashPassword(String password) {
        // 비밀번호 앞뒤 공백 제거
        String trimmedPassword = password.trim();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(trimmedPassword.getBytes());  // 공백 제거 후 해시 처리
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해시 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 기존 사용자 중복 확인
    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }


    // 로그인 처리 (비즈니스 로직 수행)
    public Optional<User> authenticate(String userId, String password) {
        String hashedPassword = hashPassword(password);
        Optional<User> user = userRepository.findByUserId(userId);

        // 사용자 존재 여부와 비밀번호 일치 여부 확인
        if (user.isPresent() && checkPassword(user.get(), hashedPassword)) {
            return user; // 인증 성공
        }

        return Optional.empty(); // 인증 실패
    }

    // 비밀번호 확인 메서드
    private boolean checkPassword(User user, String enteredPasswordHash) {
        String storedPasswordHash = user.getPassword();

        return storedPasswordHash.equals(enteredPasswordHash);
    }

    // 로그아웃 처리
    public String logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "로그아웃되었습니다.";
    }

    // 전화번호 변경 처리
    @Transactional
    public String changePhoneNumber(String userId, String newPhoneNumber) {
        int rowsUpdated = userRepository.updatePhoneNumber(userId, newPhoneNumber);
        if (rowsUpdated > 0) {
            return "전화번호가 성공적으로 변경되었습니다.";
        } else {
            throw new IllegalStateException("전화번호 변경에 실패했습니다. 유저를 찾을 수 없습니다.");
        }
    }

    // 회원 탈퇴 처리 (대여 중인 도서가 없을 때만 탈퇴 가능)
    @Transactional
    public String deleteAccount(String userId) {
        // 사용자 조회
        Optional<User> user = userRepository.findByUserId(userId);

        int rentCount = user.get().getUserRentCount();

        // 대여 중인 도서가 있을 경우
        if (rentCount > 0) {
            return "대여 중인 도서가 있어 탈퇴할 수 없습니다.";
        }

        // 회원 삭제
        userRepository.deleteByUserId(userId);
        return "회원 탈퇴가 완료되었습니다.";
    }

    // 모든 회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 대여 권수 제한을 초과하지 않은 회원 조회
    public List<User> findUsersByRentLimit(int maxRentCount) {
        return userRepository.findByUserRentCountLessThanEqual(maxRentCount);
    }

    // 관리자가 회원을 삭제하는 메서드
    public String removeUserByAdmin(Long id) {
        userRepository.deleteById(id);
        return "회원이 삭제되었습니다.";
    }

    // 마이페이지 정보 가져오기
    public User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return (User) session.getAttribute("user");
        }
        return null; // 로그인되지 않은 경우
    }
}
