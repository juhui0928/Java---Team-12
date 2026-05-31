package dormmate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * [룸메이트 매칭 기능 담당 클래스]
 * 역할: students.txt 파일에서 학생들을 불러오고, 
 * 내가 입력한 성향과 비교해서 가장 잘 맞는 사람을 찾아주는 로직
 */

public class MatchingService {
    private List<User> userList;//시스템에 등록된 전체 학생 관리 리스트
    private final String fileName = "students.txt"; //학생 데이터가 저장된 텍스트 파일

    public MatchingService() {
        this.userList = new ArrayList<>();
        loadStudentsFromFile();//시스템 시작 시 학생들의 초기 데이터를 자동으로 읽어옴
    }
    /**
     * [주요 기능: 데이터 파싱 및 파일 입출력 로직]
     * students.txt 파일을 열어서 한 줄씩 읽은 뒤, 
     * 슬래시(/)를 기준으로 잘라서 학생 객체(User)로 만들어 리스트에 넣습니다.
     * 예외 처리: 파일 미존재 및 입출력 장애(IOException), 파싱 에러(NumberFormatException)
     */
    private void loadStudentsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;//무효 라인 및 공백 스킵
                
                String[] tokens = line.split("/");
                //새로운 데이터 포맷 정의 검증(학번/이름/성별/성향 변수 7개 총 10개의 토큰)
                if (tokens.length == 10) {
                    String id = tokens[0];
                    String name = tokens[1];
                    String gender = tokens[2];
                    
                    int smoking = Integer.parseInt(tokens[3]);
                    int drinking = Integer.parseInt(tokens[4]);
                    int sleep = Integer.parseInt(tokens[5]);
                    int cleaning = Integer.parseInt(tokens[6]);
                    int noise = Integer.parseInt(tokens[7]);
                    int call = Integer.parseInt(tokens[8]);
                    int eating = Integer.parseInt(tokens[9]);

                    Preference pref = new Preference(smoking, drinking, sleep, cleaning, noise, call, eating);
                    userList.add(new User(id, name, gender, pref));
                }
            }
            System.out.println("[성공] 파일에서 " + userList.size() + "명의 학생 데이터를 불러왔습니다.");
        } catch (IOException e) {
            System.err.println("[오류] 학생 데이터 파일을 읽어오는 중 문제가 발생했습니다: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("[오류] 파일 데이터의 숫자 형식이 잘못되었습니다.");
        }
    }

    /**
     * [학번 중복 체크]
     * 이미 등록된 학번이 있는지 리스트를 한 바퀴 돌면서 확인합니다.
     */
    public boolean isDuplicateId(String id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return true; //동일학 학번이 발견된 경우
            }
        }
        return false;
    }

    /**
     * [중복 데이터 덮어쓰기]
     * 이미 등록된 학번일 때, 새로 입력한 성향으로 리스트를 수정하고
     * 텍스트 파일(students.txt) 전체를 새로 깔끔하게 저장합니다.
     */
    public void updateUserAndFile(User updatedUser) throws IOException {
        // 1. 메모리 리스트에서 기존 학번을 찾아 새 정보로 교체
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(updatedUser.getId())) {
                userList.set(i, updatedUser); // 덮어쓰기
                break;
            }
        }

        // 2. 파일 전체를 새로 쓰기
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, false))) { // false로 지정하여 새로 작성
            for (int i = 0; i < userList.size(); i++) {
                User u = userList.get(i);
                Preference p = u.getPreference();
                
                String dataLine = String.format("%s/%s/%s/%d/%d/%d/%d/%d/%d/%d", 
                        u.getId(), u.getName(), u.getGender(), 
                        p.getSmoking(), p.getDrinking(), p.getSleep(), 
                        p.getCleaning(), p.getNoise(), p.getCall(), p.getEating());
                
                pw.print(dataLine);
                // 마지막 줄이 아니라면 다음 줄을 위한 줄바꿈(줄띄움) 추가
                if (i < userList.size() - 1) {
                    pw.println();
                }
            }
        }
    }

    public void addUser(User user) {
        userList.add(user);
    }
    
    /**
     * [매칭 결과를 정렬하기 위한 내부 클래스]
     * 차이 점수가 가장 낮은 사람을 맨 위에 올리기 위해 오름차순 정렬 조건을 설정함
     */

    class MatchResult implements Comparable<MatchResult> {
        String name;
        int diffScore;

        public MatchResult(String name, int diffScore) {
            this.name = name;
            this.diffScore = diffScore;
        }

        @Override
        public int compareTo(MatchResult o) {
        	// 차이 점수가 작을수록 나랑 잘 맞는 사람이므로 작은 순서대로 정렬(오름차순)
            return Integer.compare(this.diffScore, o.diffScore);
        }
    }
    
    /**
     * [매칭 점수 계산 알고리즘]
     * 1. 나랑 성별이 다르면 계산 안 하고 패스 (동성 매칭 규칙)
     * 2. 우리 팀이 정한 우선순위 가중치를 곱해서 성향 차이 점수를 구함
     * 3. 점수 차이가 가장 적은 사람 순서대로 정렬해서 반환
     */
    public List<String> getMatchResults(User currentUser) {
        List<MatchResult> results = new ArrayList<>();
        Preference my = currentUser.getPreference();

        for (User other : userList) {
            if (other.getId().equals(currentUser.getId())) continue;//자기 자신 제외
            if (!other.getGender().equals(currentUser.getGender())) continue;//이성 간 매칭 차단

            Preference o = other.getPreference();
            
            //중요도 지표를 배수로 설정하여 변별력 높임
            //성향 편차 점수가 0에 가까울수록 상호 간 생활패턴이 일치
            int score = Math.abs(my.getSmoking() - o.getSmoking()) * 20//흡연
                      + Math.abs(my.getDrinking() - o.getDrinking()) * 15//음주
                      + Math.abs(my.getSleep() - o.getSleep()) * 10//취침
                      + Math.abs(my.getCleaning() - o.getCleaning()) * 7//청소
                      + Math.abs(my.getNoise() - o.getNoise()) * 7//소음
                      + Math.abs(my.getCall() - o.getCall()) * 4//실내 통화
                      + Math.abs(my.getEating() - o.getEating()) * 2;//실내 취식

            results.add(new MatchResult(other.getName(), score));
        }
        //수집된 매칭 후보군 목록을 차이 점수 기반으로 정렬
        Collections.sort(results);
        //정렬 결과를 문자열 포맷으로 변환하여 화면에 띄워주기 위해 반환
        List<String> output = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            MatchResult r = results.get(i);
            if (i == 0) {
                output.add(String.format("👑 [최적의 룸메이트] %s님 (차이 점수: %d점)", r.name, r.diffScore));
            } else {
                output.add(String.format(" - %s님 (차이 점수: %d점)", r.name, r.diffScore));
            }
        }
        
        if (results.isEmpty()) {
            output.add("매칭 가능한 동성 학생 데이터가 없습니다.");
        } else {
            output.add("\n※ 차이 점수가 0에 가까울수록 성향이 일치합니다.");
        }
        return output;
    }
}