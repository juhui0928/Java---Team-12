package dormmate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * [메인 GUI 화면 클래스]
 * 프로그램의 전체 화면을 구성하며, JTabbedPane을 이용하여
 * 성향 입력, 룸메이트 매칭, 세탁기 예약, 익명 게시판 4가지 탭을 제공합니다.
 */
public class MainFrame extends JFrame {
    // 기능별 비즈니스 로직을 처리하는 서비스 객체들
    private MatchingService matchingService;
    private LaundrySystem laundrySystem;
    private BoardService boardService;
    
    // 현재 로그인(성향 입력)한 사용자의 정보를 담는 객체
    private User currentUser;

    // 1번 탭 (나의 성향 입력) UI 부품
    private JTextField nameField, idField;
    private JComboBox<String> genderCombo; 
    private JComboBox<String> smokeCombo, drinkCombo, sleepCombo, cleanCombo, noiseCombo, callCombo, eatCombo;
    
    // 3번 탭 (세탁기 예약) 인적사항 입력 필드
    private JTextField laundryIdField, laundryNameField;
    
    // 4번 탭 (익명 게시판) 전용 UI 부품
    private DefaultListModel<String> boardListModel; // 화면에 보이는 리스트 데이터를 관리하는 모델
    private JList<String> boardList;                 // 실제 화면에 그려지는 리스트 컴포넌트
    private JTextArea postContentArea;               // 글 작성 텍스트 구역
    
    // 공통 사용 (시스템 진행 상황을 보여주는 하단 로그 창)
    private JTextArea logArea;

    /**
     * 메인 프레임 생성자 (프로그램 시작 시 호출됨)
     */
    public MainFrame() {
        // 각 서비스 기능 초기화
        matchingService = new MatchingService();
        laundrySystem = new LaundrySystem(7); // 총 7대의 세탁기 가동
        boardService = new BoardService();    // 게시판 서비스 가동

        // 기본 윈도우 창 설정
        setTitle("기숙사 룸메이트 매칭 & 예약 시스템 v3 (익명 게시판 포함)");
        setSize(600, 720); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 탭 패널 생성 및 각각의 화면을 부착
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("나의 성향 입력", createProfilePanel());
        tabs.addTab("룸메이트 매칭", createMatchPanel());
        tabs.addTab("세탁기 예약", createLaundryPanel());
        tabs.addTab("익명 게시판", createBoardPanel()); // ✨ 새롭게 추가된 게시판 탭

        // 중앙에 탭 배치
        add(tabs, BorderLayout.CENTER);
        
        // 하단에 시스템 로그를 보여주는 텍스트 에어리어 배치
        logArea = new JTextArea(5, 50);
        logArea.setEditable(false); // 사용자가 임의로 텍스트를 수정하지 못하게 막음
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
    }

    // =========================================================
    // [탭 1] 나의 성향 입력 화면 구성
    // =========================================================
    private JPanel createProfilePanel() {
        JPanel p = new JPanel(new GridLayout(11, 2, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("이름:")); p.add(nameField = new JTextField());
        p.add(new JLabel("학번:")); p.add(idField = new JTextField());
        p.add(new JLabel("성별:")); p.add(genderCombo = new JComboBox<>(new String[]{"남", "여"}));

        p.add(new JLabel("흡연 여부:")); p.add(smokeCombo = new JComboBox<>(new String[]{"비흡연 (0)", "흡연 (1)"}));
        p.add(new JLabel("음주 빈도 (1~5): ")); p.add(drinkCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}));
        p.add(new JLabel("수면 시간 (1~5, 일찍~늦게): ")); p.add(sleepCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}));
        p.add(new JLabel("청소 주기 (1~5, 매일~안함): ")); p.add(cleanCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}));
        p.add(new JLabel("실내 소음 (1~5, 조용~소란): ")); p.add(noiseCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}));
        p.add(new JLabel("통화 빈도 (1~5, 안함~자주): ")); p.add(callCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}));
        p.add(new JLabel("실내 취식 (0~1, 불가~가능): ")); p.add(eatCombo = new JComboBox<>(new String[]{"불가 (0)", "가능 (1)"}));

        JButton saveBtn = new JButton("성향 저장 및 등록");
        saveBtn.addActionListener(e -> saveProfile());
        p.add(saveBtn);

        return p;
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();

        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름과 학번을 입력해주세요.");
            return;
        }

        int smoke = smokeCombo.getSelectedIndex();
        int drink = Integer.parseInt((String) drinkCombo.getSelectedItem());
        int sleep = Integer.parseInt((String) sleepCombo.getSelectedItem());
        int clean = Integer.parseInt((String) cleanCombo.getSelectedItem());
        int noise = Integer.parseInt((String) noiseCombo.getSelectedItem());
        int call = Integer.parseInt((String) callCombo.getSelectedItem());
        int eat = eatCombo.getSelectedIndex();

        Preference pref = new Preference(smoke, drink, sleep, clean, noise, call, eat);
        currentUser = new User(id, name, gender, pref);

        matchingService.registerOrUpdateUser(currentUser);

        logArea.append("[성향등록] " + name + "(" + id + ") 등록 완료\n");
        JOptionPane.showMessageDialog(this, "성향 정보가 파일에 저장되었습니다.");
    }

    // =========================================================
    // [탭 2] 룸메이트 매칭 화면 구성
    // =========================================================
    private JPanel createMatchPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton matchBtn = new JButton("추천 룸메이트 찾기 (성별 일치 기준)");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        matchBtn.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "먼저 성향 입력을 완료해주세요.");
                return;
            }
            matchingService.reloadUsers();
            List<String> rank = matchingService.getTopMatches(currentUser);
            
            resultArea.setText("");
            if (rank.isEmpty()) {
                resultArea.append("매칭 가능한 동일 성별의 학생이 없습니다.\n");
            } else {
                resultArea.append("=== " + currentUser.getName() + "님을 위한 룸메이트 매칭 순위 ===\n\n");
                for (String s : rank) {
                    resultArea.append(s + "\n");
                }
            }
        });

        p.add(matchBtn, BorderLayout.NORTH);
        p.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        return p;
    }

    // =========================================================
    // [탭 3] 세탁기 예약 화면 구성
    // =========================================================
    private JPanel createLaundryPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new GridLayout(2, 2, 5, 5));
        top.add(new JLabel("예약자 학번:")); top.add(laundryIdField = new JTextField());
        top.add(new JLabel("예약자 이름:")); top.add(laundryNameField = new JTextField());
        p.add(top, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 3, 10, 10));
        List<LaundryMachine> list = laundrySystem.getMachines();
        for (LaundryMachine m : list) {
            JButton btn = new JButton(m.getId() + "번 세탁기");
            btn.addActionListener(e -> showLaundryTimeTable(m.getId()));
            grid.add(btn);
        }
        p.add(grid, BorderLayout.CENTER);

        return p;
    }

    private void showLaundryTimeTable(int machineNum) {
        String laundryId = laundryIdField.getText().trim();
        String laundryName = laundryNameField.getText().trim();

        if (laundryId.isEmpty() || laundryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "세탁기 이용을 위해 학번과 이름을 입력해주세요.");
            return;
        }

        LaundryMachine machine = laundrySystem.getMachine(machineNum);
        String[] slots = laundrySystem.getTimeSlots();

        JPanel slotPanel = new JPanel(new GridLayout(slots.length, 1, 5, 5));

        for (int i = 0; i < slots.length; i++) {
            final int slotIdx = i;
            String status = machine.isReserved(slotIdx) ? "[" + machine.getReservedUser(slotIdx) + "]" : "[예약 가능]";
            JButton slotBtn = new JButton(slots[i] + " " + status);
            
            if (machine.isReserved(slotIdx)) {
                slotBtn.setBackground(Color.PINK);
            } else {
                slotBtn.setBackground(Color.WHITE);
            }

            slotBtn.addActionListener(e -> {
                if (!machine.isReserved(slotIdx)) {
                    if (machine.reserve(slotIdx, laundryId, laundryName)) {
                        JOptionPane.showMessageDialog(null, slots[slotIdx] + " 예약되었습니다.");
                        logArea.append("[예약] " + machineNum + "번 세탁기 " + slots[slotIdx] + " 완료 (학번: " + laundryId + " / 이름: " + laundryName + ")\n");
                    }
                } else {
                    if (machine.cancel(slotIdx, laundryId)) {
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

    // =========================================================
    // [탭 4] ✨ 익명 게시판 화면 구성 (최적화 버전)
    // =========================================================
    /**
     * 익명 게시판 탭의 UI를 구성하는 메서드입니다.
     * 상단에는 글 목록, 하단에는 글 작성 및 조작 버튼을 배치합니다.
     */
    private JPanel createBoardPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 상단: 익명 글 목록 영역
        boardListModel = new DefaultListModel<>(); // JList의 데이터를 담을 빈 모델 생성
        boardList = new JList<>(boardListModel);   // 데이터를 화면에 뿌려줄 리스트 뷰 생성
        
        // 화면을 처음 생성할 때 파일에서 데이터를 읽어와 리스트 모델에 채웁니다.
        refreshBoard();

        JScrollPane listScrollPane = new JScrollPane(boardList); // 리스트가 길어지면 스크롤이 생기도록 감싸줌
        listScrollPane.setBorder(BorderFactory.createTitledBorder("기숙사 대나무숲 (익명 글 목록)"));
        p.add(listScrollPane, BorderLayout.CENTER); // 패널의 가운데에 꽉 차게 배치

        // 2. 하단: 글 작성 텍스트 영역 및 버튼 영역
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        postContentArea = new JTextArea(3, 40);
        postContentArea.setLineWrap(true); // 작성 시 가로 끝에 닿으면 자동으로 줄바꿈 되도록 설정
        JScrollPane contentScrollPane = new JScrollPane(postContentArea);
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("실시간 익명 제보 쓰기"));
        bottomPanel.add(contentScrollPane, BorderLayout.CENTER);

        // 글 등록과 새로고침을 위한 우측 버튼 영역
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton writeBtn = new JButton("글 올리기 ✍️");
        JButton refreshBtn = new JButton("새로고침 🔄"); // 다른 유저의 글을 읽어오기 위한 동기화 버튼
        
        btnPanel.add(writeBtn);
        btnPanel.add(refreshBtn);
        bottomPanel.add(btnPanel, BorderLayout.EAST); // 하단 패널의 오른쪽에 버튼 배치
        
        p.add(bottomPanel, BorderLayout.SOUTH); // 전체 패널의 하단에 조작 영역 부착

        // ---------------------------------------------------------
        // [이벤트] '글 올리기' 버튼 클릭 시 동작 설정
        // ---------------------------------------------------------
        writeBtn.addActionListener(e -> {
            String content = postContentArea.getText().trim(); // 사용자가 쓴 내용을 가져옴
            
            // 공백을 방지하기 위한 예외 처리
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "내용을 입력해주세요.");
                return;
            }
            
            // 파일 양식이 깨지는 것을 방지하기 위해 사용자가 친 엔터키(
)를 공백으로 변환하여 서비스에 전달
            boardService.addPost(content.replace("\n", " "));
            
            // 글 저장이 완료되면 자동으로 최신 목록을 불러와 화면에 적용
            refreshBoard(); 
            
            // 입력창 초기화 및 로그 기록
            postContentArea.setText("");
            logArea.append("[게시판] 새로운 익명 게시글이 등록되었습니다.\n");
        });

        // ---------------------------------------------------------
        // [이벤트] '새로고침' 버튼 클릭 시 동작 설정
        // ---------------------------------------------------------
        refreshBtn.addActionListener(e -> {
            // 다른 컴퓨터나 프로세스에서 추가된 글을 확인하기 위해 수동 동기화 실행
            refreshBoard();
            logArea.append("[게시판] 최신 익명 글을 불러왔습니다.\n");
        });

        return p;
    }

    /**
     * [게시판 목록 새로고침 도우미 메서드]
     * 현재 화면에 떠있는 리스트(boardListModel)를 싹 지우고, 
     * BoardService를 통해 텍스트 파일에서 최신 글을 다시 읽어와서 다시 뿌려줍니다.
     */
    private void refreshBoard() {
        boardListModel.clear(); // 기존 화면 데이터 비우기
        
        // boardService가 파일에서 읽어온 최신 리스트를 반복문으로 돌면서 하나씩 추가
        for (BoardPost post : boardService.getPostList()) {
            boardListModel.addElement(post.toString());
        }
    }
}
