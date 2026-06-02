package dormmate;

/**
 * [익명 게시글 데이터 모델 클래스]
 * 게시판에 올라오는 글 1개의 데이터를 담는 역할을 하는 DTO(Data Transfer Object) 클래스입니다.
 */
public class BoardPost {
    // 게시글 내용을 저장하는 변수
    private String content;

    /**
     * 객체 생성 시 게시글 내용을 초기화하는 생성자입니다.
     * @param content 사용자가 작성한 글 내용
     */
    public BoardPost(String content) {
        this.content = content;
    }

    /**
     * 저장된 게시글 내용을 반환합니다.
     * @return 게시글 내용 문자열
     */
    public String getContent() {
        return content;
    }

    /**
     * 화면(JList)에 출력될 때 보여질 문자열 형식을 정의합니다.
     * 익명 게시판이므로 내용 앞에 "[익명]" 태그를 붙여서 반환합니다.
     */
    @Override
    public String toString() {
        return "[익명] " + content;
    }
}
