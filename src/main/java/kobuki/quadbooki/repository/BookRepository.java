package kobuki.quadbooki.repository;


import kobuki.quadbooki.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 관리자에 의해 도서 추가 및 삭제 (기본 CRUD 메서드 사용)
    // 도서 추가 - JpaRepository의 save() 메서드를 사용
    // 도서 삭제 - JpaRepository의 delete() 메서드를 사용

    // 메서드 이름 기반 쿼리
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);

    // @Query로 구현
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);

    // ISBN으로 도서 찾기
    Optional<Book> findByEaIsbn(String eaIsbn);

    // 제목으로 도서 찾기
    List<Book> findByTitleContaining(String title);

    // 저자로 도서 찾기
    List<Book> findByAuthorContaining(String author);

    // 주제로 도서 찾기
    List<Book> findBySubject(String subject);

    // 출판사로 도서 찾기
    List<Book> findByPublisherContaining(String publisher);

    // 대여 가능한 도서 찾기
    List<Book> findByIsRentedFalse();

    // 현재 대여 중인 도서 찾기
    List<Book> findByIsRentedTrue();

    // 동일한 ISBN의 도서가 이미 존재하는지 확인
    boolean existsByEaIsbn(String eaIsbn);

    List<Book> findByTitleContainingIgnoreCase(String title);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
