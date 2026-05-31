package dormmate;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * [메인 GUI 화면 클래스]
 * 역할: 프로그램의 전체 메인 창을 생성하고, JTabbedPane을 이용해
 * 성향 입력, 룸메이트 매칭, 세탁기 예약 3가지 탭 화면을 유기적으로 연결합니다.
 */

public class MainFrame extends JFrame {
	//기능별 서비스 클래스 및 현재 로그인 유저 객체 선언
    private MatchingService matchingService;
    private LaundrySystem laundrySystem;
    private User currentUser;//나의 성향 입력 탭에서 저장된 사용자를 기억함

    //성향 입력 탭 GUI 부품들
    private JTextField nameField, idField;
    private JComboBox<String> genderCombo; 
    private JComboBox<String> smokeCombo, drinkCombo, sleepCombo, cleanCombo, noiseCombo, callCombo, eatCombo;
    private JTextArea logArea;//화면 맨 아래 실시간 시스템 상황을 텍스트로 보여주는 로그창
    
    //세탁기 예약 탭 전용 인적사항 입력창
    private JTextField laundryIdField, laundryNameField;
    
    //학생들의 데이터 누적 기록용 텍스트 파일
    private final String fileName = "students.txt"; 

    public MainFrame() {
    	//프로그램 실행 시 매칭 서비스 및 세탁기 7대 관리 시스템 초기화
        matchingService = new MatchingService();
        laundrySystem = new LaundrySystem(7); 
        
        //메인 프레임 창의 기본적인 레이아웃 및 속성 문의 
        setTitle("기숙사 룸메이트 매칭 & 예약 시스템 v2");
        setSize(600, 680); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);//x버튼 클릭 시 프로세스 종료
        setLayout(new BorderLayout());//동서남북 구조의 레이아웃 배치 방식
        
        //3개의 주요 화면을 상단 탭 메뉴형태로 구성
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("나의 성향 입력", createProfilePanel());
        tabs.addTab("룸메이트 매칭", createMatchPanel());
        tabs.addTab("세탁기 예약", createLaundryPanel());

        add(tabs, BorderLayout.CENTER);
        
        //화면 하단에 스크롤 기능이 포함된 실시간 시스템 모니터링 로그창
        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);//사용자가 임의로 글씨를 못쓰게 읽기 전용으로 설정
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
        /*
         * 프로그램 종료 시 자동 백업
         * 세탁기 예약 정보를 CSV 파일에 저장
         */
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(
                    java.awt.event.WindowEvent e) {

                laundrySystem.saveReservations();

                System.out.println(
                        "[시스템] 예약 정보 저장 완료"
                );
            }
        });
    }
    /**
     * [화면 구성 1: 나의 성향 설문 입력 패널]
     *GridLayout(11, 2) 방식을 사용하여 이름, 학번, 성별 및 성향 데이터 폼을 바둑판 배열로 정렬함.
     * 점수나 숫자로 표기되던 가독성 떨어지는 메뉴를 한글 명사형 스케일 배열로 매핑해 직관성을 높임.
     */

    private JPanel createProfilePanel() {
        JPanel p = new JPanel(new GridLayout(11, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        String[] drinkScale = {"전혀 안 함", "가끔", "보통", "자주", "매우 자주"};
        String[] sleepScale = {"11시 이전", "11시~12시", "12시~1시", "1시~2시", "2시 이후"};
        String[] cleanScale = {"거의 안 함", "월 1~2회", "주 1회", "주 2~3회", "매일"};
        String[] noiseScale = {"매우 둔감", "약간 둔감", "보통", "약간 예민", "매우 예민"};
        String[] callScale = {"절대 불가", "간단한 용건만", "보통", "자주 함", "항상 함"};

        p.add(new JLabel("이름:")); nameField = new JTextField(); p.add(nameField);
        p.add(new JLabel("학번 (10자리):")); idField = new JTextField(); p.add(idField);
        p.add(new JLabel("성별:")); genderCombo = new JComboBox<>(new String[]{"남", "여"}); p.add(genderCombo); 
        
        p.add(new JLabel("흡연 여부:")); smokeCombo = new JComboBox<>(new String[]{"비흡연", "흡연"}); p.add(smokeCombo);
        p.add(new JLabel("음주 빈도:")); drinkCombo = new JComboBox<>(drinkScale); p.add(drinkCombo);
        p.add(new JLabel("수면 시간:")); sleepCombo = new JComboBox<>(sleepScale); p.add(sleepCombo);
        p.add(new JLabel("청소 주기:")); cleanCombo = new JComboBox<>(cleanScale); p.add(cleanCombo);
        p.add(new JLabel("소음 민감도:")); noiseCombo = new JComboBox<>(noiseScale); p.add(noiseCombo);
        p.add(new JLabel("실내 통화:")); callCombo = new JComboBox<>(callScale); p.add(callCombo);
        p.add(new JLabel("실내 취식:")); eatCombo = new JComboBox<>(new String[]{"싫음", "가능"}); p.add(eatCombo);

        JButton saveBtn = new JButton("내 정보 저장");
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem(); 
            
            //이름이나 학번칸 공백 확인
            if(name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름과 학번을 입력하세요!");
                return;
            }
            //학번 규격에 맞는지 검사
            if (id.length() != 10) {
                JOptionPane.showMessageDialog(this, "학번은 정확히 10자리여야 합니다!\n다시 입력해주세요.", "학번 입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //매칭 연산용 데이터
            int smoking = smokeCombo.getSelectedIndex();
            int drinking = drinkCombo.getSelectedIndex() + 1;
            int sleep = sleepCombo.getSelectedIndex() + 1;
            int cleaning = cleanCombo.getSelectedIndex() + 1;
            int noise = noiseCombo.getSelectedIndex() + 1;
            int call = callCombo.getSelectedIndex() + 1;
            int eating = eatCombo.getSelectedIndex();

            //입력받은 정보들을 토대로한 구조화된 임시 유저 데이터 인스턴스
            Preference pref = new Preference(smoking, drinking, sleep, cleaning, noise, call, eating);
            User targetUser = new User(id, name, gender, pref);

            // 💡 [덮어쓰기 분기 로직 반영]
            // 동일한 학번이 이미 등록되어 있는지 조회
            if (matchingService.isDuplicateId(id)) {
                int reply = JOptionPane.showConfirmDialog(this, 
                        "이미 입력된 사람입니다. 기존 정보를 덮어씌우시겠습니까?", 
                        "중복 데이터 확인", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                
                if (reply == JOptionPane.YES_OPTION) {
                    try {
                        // '예'를 누르면 기존 목록에서 찾아서 수정 후 파일을 새로 씁니다.
                        matchingService.updateUserAndFile(targetUser);
                        currentUser = targetUser; // 현재 세션 유저 업데이트
                        
                        logArea.append("저장되었습니다.\n");
                        JOptionPane.showMessageDialog(this, "저장되었습니다.");
                    } catch (IOException ex) {
                        logArea.append("[오류] 파일 업데이트 중 문제가 발생했습니다: " + ex.getMessage() + "\n");
                        JOptionPane.showMessageDialog(this, "정보 수정에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // '아니오'를 누르면 아무것도 하지 않고 화면 유지
                return; 
            }

            // 중복 데이터가 아닐 때만 수행하는 기본 신규 추가 로직
            currentUser = targetUser;
            matchingService.addUser(currentUser);
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
                pw.println(); // 새로 추가 시 첫 칸 줄띄움
                
                String dataLine = String.format("%s/%s/%s/%d/%d/%d/%d/%d/%d/%d", 
                        id, name, gender, smoking, drinking, sleep, cleaning, noise, call, eating);
                pw.print(dataLine);
                
                logArea.append("저장되었습니다.\n");
                JOptionPane.showMessageDialog(this, "저장되었습니다.");
            } catch (IOException ex) {
                logArea.append("[오류] 파일 저장 중 문제가 발생했습니다: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "파일 저장에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        p.add(new JLabel("")); p.add(saveBtn);
        return p;
    }

    /**
     * [화면 구성 2: 룸메이트 매칭 결과 출력 패널]
     * 역할: 성향 매칭 버튼을 누르면 나와 동일한 성별을 가진 매칭 데이터 풀 내에서 
     * 우선순위 가중치가 곱해진 차이 점수를 내림차순 정렬하여 순위를 리스트 컴포넌트(JList)로 출력합니다.
     */
    private JPanel createMatchPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        
        JButton matchBtn = new JButton("최적의 룸메이트 매칭 시작 (차이 점수 계산)");
        matchBtn.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "먼저 '나의 성향 입력' 탭에서 프로필을 저장하세요!");
                return;
            }
            listModel.clear();
            List<String> matches = matchingService.getMatchResults(currentUser);
            for (String s : matches) listModel.addElement(s);
            logArea.append("[시스템] 가중치 기반 매칭 분석 완료.\n");
        });

        p.add(matchBtn, BorderLayout.NORTH);
        p.add(new JScrollPane(resultList), BorderLayout.CENTER);
        return p;
    }
    
    /**
     * [화면 구성 3: 세탁실 배치도 기반 예약 레이아웃 패널]
     * 구조: 상단에 독립 인적사항 입력 필드를 두고, 중앙에 세탁기 배치를 직관적으로 보이기 위해
     * 3x3 GridLayout 구조틀 내에 매트릭스 배열 설계를 적용하여 실제 'ㄷ'자 모양 세탁실 동선을 시각화함.
     */

    private JPanel createLaundryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("세탁기 이용자 정보"));
        laundryIdField = new JTextField(10); 
        laundryNameField = new JTextField(8);
        userPanel.add(new JLabel("학번:"));
        userPanel.add(laundryIdField);
        userPanel.add(new JLabel("이름:"));
        userPanel.add(laundryNameField);
        
        mainPanel.add(userPanel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createTitledBorder("세탁실 배치도 (ㄷ자)"));

        int[][] layout = {
            {1, 2, 3},
            {4, 0, 0},
            {5, 6, 7}
        };

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int machineNum = layout[r][c];
                if (machineNum == 0) {
                    gridPanel.add(new JLabel(""));
                } else {
                    JButton machineBtn = new JButton("세탁기 " + machineNum);
                    machineBtn.setBackground(new Color(230, 240, 255));
                    machineBtn.addActionListener(e -> showLaundryTimeTable(machineNum));
                    gridPanel.add(machineBtn);
                }
            }
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(new JLabel("상단에 학번과 이름을 입력하고 세탁기를 선택하세요.", JLabel.CENTER), BorderLayout.SOUTH);
        
        return mainPanel;
    }

    /**
     * [세탁기별 세부 시간표 제어 및 예약 상태 변경 모달창]
     * 동작 흐름:
     * 1. 학번과 이름의 공백 유무 및 자리수 3차 예외 테스트 수행.
     * 2. 선택한 세탁기의 예약 유무 상태 확인 후 버튼 컬러 피드백 다이내믹 변경 (PINK: 점유 / WHITE: 가용).
     * 3. [미예약 타임 클릭]: 학번 기반 고유 키 정보 전달 및 예약 생성 + 10초 실시간 시뮬레이션 타이머 가동.
     * 4. [예약 타임 클릭]: 타인 조작 차단을 위해 세탁기에 저장된 학번 정보를 추출하여 엄격한 본인 인증 후 취소 처리.
     */
    private void showLaundryTimeTable(int machineNum) {
        String laundryName = laundryNameField.getText().trim();
        String laundryId = laundryIdField.getText().trim();

        if (laundryId.isEmpty() || laundryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "세탁기 예약을 위해 학번과 이름을 상단에 입력해주세요!");
            return;
        }

        if (laundryId.length() != 10) {
            JOptionPane.showMessageDialog(this, "학번은 정확히 10자리여야 합니다!\n다시 입력해주세요.", "학번 입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LaundryMachine machine = laundrySystem.getMachine(machineNum);
        String[] slots = laundrySystem.getTimeSlots();
        
        JPanel slotPanel = new JPanel(new GridLayout(slots.length, 1, 5, 5));
        
        for (int i = 0; i < slots.length; i++) {
            int slotIdx = i;
            String status = machine.isReserved(slotIdx) ? "[" + machine.getReservedUser(slotIdx) + "]" : "[예약 가능]";
            JButton slotBtn = new JButton(slots[i] + " " + status);
            
            if (machine.isReserved(slotIdx)) {
                slotBtn.setBackground(Color.PINK);
            } else {
                slotBtn.setBackground(Color.WHITE);
            }

            slotBtn.addActionListener(e -> {
                if (!machine.isReserved(slotIdx)) {
                	if (machine.reserve(
                	        slotIdx,
                	        laundryId,
                	        laundryName)) {
                        JOptionPane.showMessageDialog(null, slots[slotIdx] + " 예약되었습니다.");
                        logArea.append("[예약] " + machineNum + "번 세탁기 " + slots[slotIdx] + " 완료 (학번: " + laundryId + " / 이름: " + laundryName + ")\n");
                    }
                	javax.swing.Timer timer =
                	        new javax.swing.Timer(
                	                10000,
                	                event -> {

                	                    machine.cancel(
                	                            slotIdx,
                	                            laundryId
                	                    );

                	                    JOptionPane.showMessageDialog(
                	                            null,
                	                            laundryName
                	                            + "님의 세탁이 완료되었습니다."
                	                    );

                	                    logArea.append(
                	                            "[사용완료] "
                	                            + machineNum
                	                            + "번 세탁기 "
                	                            + slots[slotIdx]
                	                            + " 완료\n"
                	                    );
                	                });

                	timer.setRepeats(false);
                	timer.start();
                	
                } else {
                	if (machine.cancel(
                	        slotIdx,
                	        laundryId)) {
                        JOptionPane.showMessageDialog(null, "예약이 취소되었습니다.");
                        logArea.append("[취소] " + machineNum + "번 세탁기 " + slots[slotIdx] + " 취소 (" + laundryName + ")\n");
                    } else {
                        JOptionPane.showMessageDialog(null, "본인의 예약만 취소할 수 있습니다.");
                    }
                }
                SwingUtilities.getWindowAncestor(slotPanel).dispose();
            });
            slotPanel.add(slotBtn);
        }

        JOptionPane.showOptionDialog(this, slotPanel, machineNum + "번 세탁기 예약 현황", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
    }
}