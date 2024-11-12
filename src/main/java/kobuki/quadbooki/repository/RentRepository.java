package kobuki.quadbooki.repository;


import kobuki.quadbooki.domain.Book;
import kobuki.quadbooki.domain.Rent;
import kobuki.quadbooki.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {

    // 렌트 생성 - JpaRepository의 save() 메서드를 사용하여 새로운 대여 기록 생성
    // 렌트 삭제 - JpaRepository의 delete() 메서드를 사용하여 대여 기록 삭제

    // 사용자 ID로 대여 기록 찾기
    List<Rent> findByUser(User user);

    // 도서 ID로 대여 기록 찾기
    Optional<Rent> findByBook(Book book);

    // 반납 여부로 대여 기록 찾기 (반납되지 않은 도서)
    List<Rent> findByIsReturnedFalse();

    // 반납 여부로 대여 기록 찾기 (반납된 도서)
    List<Rent> findByIsReturnedTrue();

    // 대여일 기준으로 대여 기록 찾기
    List<Rent> findByRentDate(String rentDate);

    // 대여자의 대여 가능 여부 확인
    Optional<Rent> findByUserAndIsReturnedFalse(User user);
}
