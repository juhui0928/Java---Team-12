package dormmate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [게시판 비즈니스 로직 및 파일 관리 클래스]
 * 화면(GUI)과 데이터(텍스트 파일) 사이에서 글쓰기 및 읽기 작업을 전담합니다.
 */
public class BoardService {
    // 게시글이 영구적으로 저장될 텍스트 파일의 이름입니다.
    private final String fileName = "board.txt";

    /**
     * [글 목록 전체 불러오기]
     * 다른 기기나 프로세스에서 쓴 글도 확인할 수 있도록,
     * 목록을 요청할 때마다 텍스트 파일을 새로 읽어 최신 상태를 유지합니다.
     * * @return 최신 게시글들이 담긴 리스트 (List<BoardPost>)
     */
    public List<BoardPost> getPostList() {
        List<BoardPost> list = new ArrayList<>();
        File file = new File(fileName);
        
        // 파일이 존재할 경우에만 데이터를 읽어옵니다.
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                // 파일의 끝(null)에 도달할 때까지 한 줄씩 읽어 들입니다.
                while ((line = br.readLine()) != null) {
                    // 빈 줄이 아닐 경우에만 객체로 만들어서 리스트에 추가합니다.
                    if (!line.trim().isEmpty()) {
                        list.add(new BoardPost(line));
                    }
                }
            } catch (IOException e) {
                System.out.println("[오류] 글을 불러올 수 없습니다. (파일 읽기 에러)");
            }
        }
        return list; // 완성된 최신 리스트를 화면(MainFrame)으로 전달
    }

    /**
     * [새로운 글 등록하기]
     * 사용자가 작성한 새 글을 파일에 저장합니다.
     * 기존 내용을 덮어쓰지 않고 맨 끝에 추가하기 위해 FileWriter의 append 옵션(true)을 사용합니다.
     * * @param content 사용자가 입력한 게시글 내용
     */
    public void addPost(String content) {
        // FileWriter(fileName, true) -> 파일 이어쓰기 모드 활성화 (성능 향상 및 데이터 보존)
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.println(content); // 파일의 맨 마지막 줄에 새 글을 기록합니다.
        } catch (IOException e) {
            System.out.println("[오류] 글을 저장할 수 없습니다. (파일 쓰기 에러)");
        }
    }
}
