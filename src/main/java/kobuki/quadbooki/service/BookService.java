package kobuki.quadbooki.service;

import kobuki.quadbooki.dto.BookListDto;
import kobuki.quadbooki.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final로 지정된 변수들 자동 생성자 만들어주는 것
public class BookService {
    private final BookRepository bookRepository;


    public List<BookListDto> getBooksList() {
        return bookRepository.findAll().stream().map(book -> new BookListDto(
                book.getTitle(),
                book.getAuthor(),
                book.getBookIntroductionUrl(),
                book.getTitleUrl(),
                book.isRented())).collect(Collectors.toList());
    }
}
