package kobuki.quadbooki.service;

import kobuki.quadbooki.domain.Book;
import kobuki.quadbooki.domain.Rent;
import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.repository.BookRepository;
import kobuki.quadbooki.repository.RentRepository;
import kobuki.quadbooki.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentService {
    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public RentService(RentRepository rentRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.rentRepository = rentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Rent createRent(Long userId, Long bookId, LocalDate rentDate, LocalDate rentReturnDate) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // 도서 대여 가능 여부 확인 (사용자의 대여 횟수 제한)
        if (user.getUserRentCount() >= 3) {
            throw new IllegalStateException("사용자는 최대 3권까지 대여할 수 있습니다.");
        }

        // 관리자가 도서 대여를 막아버리면.
        if (user.isUserRentable()) {
            throw new IllegalStateException("사용자는 도서 대여가 불가능합니다. 관리자에게 문의하세요.");
        }

        // 도서 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 도서 ID입니다."));

        // 도서 대여 가능 여부 확인
        if (book.isRented()) {
            throw new IllegalStateException("도서가 이미 대여 중입니다.");
        }

        // Rent 객체 생성
        Rent rent = new Rent();
        rent.setUser(user);
        rent.setBook(book);
        rent.setRentDate(rentDate);
        rent.setRentReturnDate(rentReturnDate);
        rent.setReturned(false);

        // 도서 대여 상태 업데이트
        book.setRented(true);
        bookRepository.save(book);

        // 사용자의 대여 횟수 증가
        user.setUserRentCount(user.getUserRentCount() + 1);
        userRepository.save(user);

        return rentRepository.save(rent);
    }

    public List<Rent> getRentsByUserId(Long userId) {
        return rentRepository.findByUserId(userId);
    }

    public Rent getRentById(Long rentId) {
        return rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대여 ID입니다."));
    }

    @Transactional
    public void returnBook(Long rentId) {
        // 대여 정보 조회
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대여 ID입니다."));

        // 도서 반납 처리
        rent.setReturned(true);
        Book book = rent.getBook();
        book.setRented(false);

        // 사용자의 대여 횟수 감소
        User user = rent.getUser();
        user.setUserRentCount(user.getUserRentCount() - 1);
        userRepository.save(user);

        bookRepository.save(book);
        rentRepository.save(rent);
    }
}

