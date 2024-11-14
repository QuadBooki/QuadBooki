package kobuki.quadbooki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookListDto {
    private String title;
    private String author;
    private String bookIntroductionUrl;
    private String titleUrl;
    private boolean isRented;
}
