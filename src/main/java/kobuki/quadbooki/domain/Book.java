package kobuki.quadbooki.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eaIsbn;  //ISBN
    private String title;   //표제
    private String author;  //저자
    private String bookIntroductionUrl;  //책소개
    private String titleUrl;    //표지이미지 url
    private String publisherUrl;    //출판사 홈페이지 url
    private String subject;   //주제(kdc 대분류 000-총류/100-철학/200-종교 등등)
    private String publishPredate;   //출판 예정일
    private String publisher;   //발행처(출판사?)
    private boolean isRented;    // 대여 여부


}
