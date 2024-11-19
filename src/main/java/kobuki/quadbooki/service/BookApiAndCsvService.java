package kobuki.quadbooki.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kobuki.quadbooki.domain.Book;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;



public class BookApiAndCsvService {      //Open Api를 통하여 도서정보들을 불러와 csv파일로 변환하는 서비스 클래스 입니다.

    private final String apiUrlTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    String apiKey = "c0bd39f7eb9b11856b26503aaa99ac817efaaa4f3ae4a12565ece0bc52bde2cb"; // 예시 API 키
    String csvFilePath = "C:\\Users\\alswp\\OneDrive\\바탕 화면\\books.csv"; // 저장할 CSV 파일 경로
    public BookApiAndCsvService() {
        this.apiUrlTemplate = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key=" + apiKey + "&result_style=json&page_no=%d&page_size=100&ebook_yn=Y&cip_yn=Y&deposit_yn=Y&form=전자책&order_by=ASC";
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // API 호출하여 도서 정보 받아오기
    public List<Book> fetchBooks() {
        List<Book> allBooks = new ArrayList<>();

        for (int pageNo = 1; pageNo <= 10; pageNo++) { // 총 3페이지를 반복
            String apiUrl = String.format(apiUrlTemplate, pageNo);
            System.out.println("Calling API: " + apiUrl); // API 호출 URL 출력
            String response = restTemplate.getForObject(apiUrl, String.class); // JSON 문자열로 받아옴
            List<Book> books = parseBooks(response);
            allBooks.addAll(books); // 모든 페이지의 도서 정보를 추가
            System.out.println("API Response for page " + pageNo + ": " + response);
        }

        return allBooks;
    }

    // JSON 문자열을 Book 객체 리스트로 변환
    private List<Book> parseBooks(String json) {
        List<Book> books = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode booksNode = rootNode.path("docs"); // API 응답에서 docs 필드 가져오기

            for (JsonNode bookNode : booksNode) {
                Book book = new Book();
                book.setEaIsbn(bookNode.path("EA_ISBN").asText());
                book.setTitle(bookNode.path("TITLE").asText());
                book.setAuthor(bookNode.path("AUTHOR").asText());
                book.setPublisher(bookNode.path("PUBLISHER").asText());
                book.setPublishPredate(bookNode.path("PUBLISH_PREDATE").asText());
                book.setSubject(bookNode.path("SUBJECT").asText());
                book.setBookIntroductionUrl(bookNode.path("BOOK_INTRODUCTION_URL").asText());
                book.setTitleUrl(bookNode.path("TITLE_URL").asText());
                book.setPublisherUrl(bookNode.path("PUBLISHER_URL").asText());
                // 필요한 필드가 있다면 여기에 추가로 설정할 수 있습니다.

                books.add(book); // 생성한 Book 객체를 리스트에 추가
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    // 도서 정보를 CSV 파일로 저장
    public void saveBooksToCsv(List<Book> books, String filePath) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")) {
            // CSV 헤더 작성
            writer.append("EA_ISBN,TITLE,AUTHOR,PUBLISHER,PUBLISH_PREDATE,SUBJECT,BOOK_INTRODUCTION_URL,TITLE_URL,PUBLISHER_URL\n");

            // 도서 정보 작성
            for (Book book : books) {
                // title_url이 있는 경우에만 추가
                if ((book.getTitleUrl() != null && !book.getTitleUrl().isEmpty()) &&(book.getBookIntroductionUrl() != null && !book.getBookIntroductionUrl().isEmpty())) {
                    writer.append("\"").append(book.getEaIsbn()).append("\",")
                            .append("\"").append(book.getTitle()).append("\",")
                            .append("\"").append(book.getAuthor()).append("\",")
                            .append("\"").append(book.getPublisher()).append("\",")
                            .append("\"").append(book.getPublishPredate()).append("\",")
                            .append("\"").append(book.getSubject()).append("\",")
                            .append("\"").append(book.getBookIntroductionUrl()).append("\",")
                            .append("\"").append(book.getTitleUrl()).append("\",")
                            .append("\"").append(book.getPublisherUrl()).append("\"\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        List<Book> books = fetchBooks(); // 도서 정보 가져오기
        saveBooksToCsv(books, csvFilePath); // CSV 파일에 저장
        System.out.println("도서 정보가 " + csvFilePath + "에 저장되었습니다.");
    }



}
