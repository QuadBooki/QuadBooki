package kobuki.quadbooki.service;

import kobuki.quadbooki.domain.Book;
import kobuki.quadbooki.domain.Rent;
import kobuki.quadbooki.domain.User;
import kobuki.quadbooki.repository.BookRepository;
import kobuki.quadbooki.repository.RentRepository;
import kobuki.quadbooki.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<Rent> getAllRents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rentDate").descending());
        return rentRepository.findByIsApprovedFalse(pageable);
    }

    @Transactional
    public void approveRent(Long rentId) {
        // Rent 객체 조회
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대여 ID입니다."));

        // 이미 반납된 도서는 승인 불가
        if (rent.isReturned()) {
            throw new IllegalStateException("이미 반납된 도서는 승인할 수 없습니다.");
        }

        // 승인 처리
        rent.setApproved(true); // 승인 여부를 true로 설정
        rent.setReturned(false); // 대여 상태 활성화 (반납 아님으로 설정)
        rentRepository.save(rent); // 변경 사항 저장
    }


    @Transactional
    public void rejectRent(Long rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대여 ID입니다."));

        rentRepository.delete(rent); // 요청 삭제
    }

    @Transactional(readOnly = true)
    public Page<Rent> getApprovedRents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rentDate").descending());
        return rentRepository.findByIsApprovedTrue(pageable);
    }

    @Transactional
    public void returnRent(Long rentId) {
        // Rent 조회
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 대여 ID입니다."));

        // 이미 반납된 상태인지 확인
        if (rent.isReturned()) {
            throw new IllegalStateException("이미 반납된 도서입니다.");
        }

        // Rent 상태 변경
        rent.setReturned(true); // 반납 완료 상태로 변경

        // Book 상태 변경
        Book book = rent.getBook();
        if (book != null) {
            book.setRented(false); // 대여 가능 상태로 변경
            bookRepository.save(book); // Book 변경 사항 저장
        }

        // Rent 변경 사항 저장
        rentRepository.save(rent);
    }


}

