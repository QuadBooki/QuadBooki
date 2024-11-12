package kobuki.quadbooki.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId; //사용자 id
    private String password; //비밀번호
    private String userName; //이름
    private Date birth; //생년월일
    private String phoneNumber; //핸드폰번호
    private int userRentCount;    //도서 대여 횟수
    private boolean userRentable; //사용자의 도서 대여 가능 여부
    private boolean isAdmin;  //관리자?
}
