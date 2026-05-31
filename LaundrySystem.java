import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*

* ---
* LaundrySystem 클래스
* ---
* 전체 세탁기 시스템을 관리하는 클래스
*
* 주요 기능
* 1. 세탁기 객체 생성 및 관리
* 2. 시간대 정보 제공
* 3. 세탁기 번호를 이용한 객체 조회
* 4. 예약 정보 CSV 저장
* 5. 예약 정보 CSV 복원
*
* 프로그램 종료 시 예약 정보를 저장하고,
* 프로그램 실행 시 기존 예약 정보를 복원하여
* 데이터 영속성을 제공한다.
* ---

*/

public class LaundrySystem {

/*
 * 전체 세탁기 목록
 */
private List<LaundryMachine> machines;

/*
 * 예약 가능 시간대
 */
private final String[] timeSlots = {
        "09:00 - 11:00",
        "11:00 - 13:00",
        "13:00 - 15:00",
        "15:00 - 17:00",
        "17:00 - 19:00"
};

/*
 * 예약 정보 저장 파일
 */
private final String DATABASE_FILE = "dorm_database.csv";

/*
 * 생성자
 */
public LaundrySystem(int machineCount) {

    machines = new ArrayList<>();

    for (int i = 1; i <= machineCount; i++) {
        machines.add(new LaundryMachine(i));
    }

    // 프로그램 시작 시 기존 예약 정보 복원
    loadReservations();
}

/*
 * 전체 세탁기 목록 반환
 */
public List<LaundryMachine> getMachines() {
    return machines;
}

/*
 * 시간대 반환
 */
public String[] getTimeSlots() {
    return timeSlots;
}

/*
 * 세탁기 번호로 객체 반환
 */
public LaundryMachine getMachine(int id) {
    return machines.get(id - 1);
}

/*
 * ---------------------------------------------------------
 * 예약 정보 저장
 * ---------------------------------------------------------
 */
public void saveReservations() {

    try (PrintWriter pw =
                 new PrintWriter(
                         new FileWriter(DATABASE_FILE))) {

        for (LaundryMachine machine : machines) {

            for (Integer slot : machine.getSlots().keySet()) {

                pw.println(
                        machine.getId()
                                + ","
                                + slot
                                + ","
                                + machine.getSlots().get(slot)
                );
            }
        }

        System.out.println("[성공] 예약 정보 저장 완료");

    } catch (IOException e) {

        System.out.println("[오류] 예약 정보 저장 실패");
        e.printStackTrace();
    }
}

/*
 * ---------------------------------------------------------
 * 예약 정보 복원
 * ---------------------------------------------------------
 */
public void loadReservations() {

    File file = new File(DATABASE_FILE);

    // 처음 실행이라 파일이 없으면 종료
    if (!file.exists()) {
        return;
    }

    try (BufferedReader br =
                 new BufferedReader(
                         new FileReader(file))) {

        String line;

        while ((line = br.readLine()) != null) {

            String[] token = line.split(",");

            if (token.length != 3) {
                continue;
            }

            int machineId =
                    Integer.parseInt(token[0]);

            int slotIndex =
                    Integer.parseInt(token[1]);

            String reservationInfo =
                    token[2];

            String[] userInfo =
                    reservationInfo.split("/");

            if (userInfo.length != 2) {
                continue;
            }

            String studentId =
                    userInfo[0];

            String userName =
                    userInfo[1];

            getMachine(machineId).reserve(
                    slotIndex,
                    studentId,
                    userName
            );
        }

        System.out.println("[성공] 예약 정보 복원 완료");

    } catch (Exception e) {

        System.out.println("[오류] 예약 정보 복원 실패");
        e.printStackTrace();
    }
}


}
