package kobuki.quadbooki.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate rentDate; //도서 대출일
    private LocalDate rentReturnDate; //도서반납일

    @ManyToOne(fetch = FetchType.LAZY)       //user 테이블의 id(pk)를 통해 user객체를 호출
    @JoinColumn(name = "user.id")            //fk 역할
    private User user;

    @OneToOne(fetch = FetchType.LAZY)       //book 테이블의 id(pk)를 통해 book객체를 호출
    @JoinColumn(name = "book.id")           //fk 역할
    private Book book;

    private boolean isReturned;      //도서 반납 여부
}
