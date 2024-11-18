package kobuki.quadbooki.service;

import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        checkForExistingUser(user);
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
    private void checkForExistingUser(User user) {
        Optional<User> existingUser = userRepository.findByUserIdAndPhoneNumber(user.getUserId(), user.getPhoneNumber());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("이미 등록된 사용자입니다. 아이디 또는 전화번호를 확인해주세요.");
        }
    }

    // 로그인 처리
    public String authenticate(String userId, String password) {
        // 입력된 비밀번호 해시화
        String hashedPassword = hashPassword(password);
        System.out.println("입력된 비밀번호 해시화: " + hashedPassword);  // 디버깅용 로그

        // DB에서 사용자 조회
        Optional<User> user = userRepository.findByUserId(userId);

        if (user.isPresent()) {
            // 비밀번호 검증
            if (checkPassword(user.get(), hashedPassword)) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);  // 세션에 사용자 정보 저장
                return "로그인 성공.";
            } else {
                System.out.println("비밀번호 불일치");
                return "비밀번호를 찾을 수 없습니다.";  // 비밀번호 불일치
            }
        } else {
            System.out.println("아이디가 존재하지 않음");
            return "아이디를 찾을 수 없습니다.";  // 아이디가 존재하지 않음
        }
    }


    // 비밀번호 확인 메서드
    private boolean checkPassword(User user, String enteredPasswordHash) {
        String storedPasswordHash = user.getPassword();
        System.out.println("DB 비밀번호 해시: " + storedPasswordHash);
        System.out.println("입력된 비밀번호 해시: " + enteredPasswordHash);

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
    public String changePhoneNumber(String userId, String newPhoneNumber) {
        Optional<User> user = userRepository.findByUserIdAndPhoneNumber(userId, newPhoneNumber);
        if (user.isPresent()) {
            throw new IllegalStateException("이 전화번호는 이미 등록되어 있습니다.");
        }
        userRepository.updatePhoneNumber(userId, newPhoneNumber);
        return "전화번호가 변경되었습니다.";
    }

    // 회원 탈퇴 처리 (대여 중인 도서가 없을 때만 탈퇴 가능)
    public String deleteAccount(String userId) {
        Optional<User> user = userRepository.findByUserIdAndUserRentCount(userId, 0);
        if (user.isEmpty()) {
            return "대여 중인 도서가 있어 탈퇴할 수 없습니다.";
        }
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
}
