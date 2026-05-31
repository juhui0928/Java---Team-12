import java.util.HashMap;

/*

* ---
* LaundryMachine 클래스
* ---
* 세탁기 1대의 예약 상태를 관리하는 클래스
*
* 기능
* 1. 시간대별 예약 관리
* 2. 예약 여부 확인
* 3. 예약자 조회
* 4. 본인 확인 후 예약 취소
* 5. CSV 파일 저장을 위한 예약 정보 제공
*
* 예약 정보는 "학번/이름" 형식으로 저장하여
* 동일 이름 사용자가 존재하더라도
* 학번을 기준으로 정확하게 본인 확인이 가능하도록 구현하였다.
* ---

*/

public class LaundryMachine {

/*
 * 세탁기 번호
 *
 * 예)
 * 1번 세탁기
 * 2번 세탁기
 * ...
 * 7번 세탁기
 */
private int id;

/*
 * 시간대별 예약 정보를 저장하는 HashMap
 *
 * Key   : 시간대 인덱스
 *         0 = 09:00 ~ 11:00
 *         1 = 11:00 ~ 13:00
 *         2 = 13:00 ~ 15:00
 *         3 = 15:00 ~ 17:00
 *         4 = 17:00 ~ 19:00
 *
 * Value : "학번/이름"
 *
 * 예)
 * 2023000001/홍길동
 */
private Map<Integer, String> slots;

/*
 * 생성자
 *
 * 세탁기 번호를 전달받아 저장하고
 * 예약 정보를 저장할 HashMap 생성
 */
public LaundryMachine(int id) {

    this.id = id;
    this.slots = new HashMap<>();
}

/*
 * 세탁기 번호 반환
 */
public int getId() {
    return id;
}

/*
 * ---------------------------------------------------------
 * 예약 기능
 * ---------------------------------------------------------
 *
 * 매개변수
 * slotIndex : 예약할 시간대
 * studentId : 예약자 학번
 * userName  : 예약자 이름
 *
 * 반환값
 * true  : 예약 성공
 * false : 이미 예약된 시간대
 *
 * 처리 과정
 * 1. 해당 시간대 예약 여부 확인
 * 2. 비어있으면 예약 정보 저장
 * 3. 이미 예약되어 있으면 실패
 * ---------------------------------------------------------
 */
public boolean reserve(
        int slotIndex,
        String studentId,
        String userName) {

    if (slots.containsKey(slotIndex)) {
        return false;
    }

    String reservationInfo =
            studentId + "/" + userName;

    slots.put(slotIndex, reservationInfo);

    return true;
}

/*
 * ---------------------------------------------------------
 * 예약 취소 기능
 * ---------------------------------------------------------
 *
 * 학번을 이용한 본인 확인 수행
 *
 * 동일 이름 사용자가 존재하는 경우에도
 * 학번이 일치해야만 취소 가능
 *
 * 반환값
 * true  : 취소 성공
 * false : 취소 실패
 * ---------------------------------------------------------
 */
public boolean cancel(
        int slotIndex,
        String studentId) {

    /*
     * 예약 자체가 존재하지 않음
     */
    if (!slots.containsKey(slotIndex)) {
        return false;
    }

    /*
     * 저장된 예약 정보
     * 예)
     * 2023000001/홍길동
     */
    String reservationInfo =
            slots.get(slotIndex);

    /*
     * "/" 기준 분리
     */
    String[] token =
            reservationInfo.split("/");

    String reservedStudentId =
            token[0];

    /*
     * 본인 확인 성공
     */
    if (reservedStudentId.equals(studentId)) {

        slots.remove(slotIndex);

        return true;
    }

    /*
     * 다른 사용자의 예약
     */
    return false;
}

/*
 * ---------------------------------------------------------
 * 예약자 이름 반환
 * ---------------------------------------------------------
 *
 * GUI 화면에서
 * [홍길동]
 * 형태로 출력하기 위해 사용
 * ---------------------------------------------------------
 */
public String getReservedUser(int slotIndex) {

    if (!slots.containsKey(slotIndex)) {
        return null;
    }

    String reservationInfo =
            slots.get(slotIndex);

    return reservationInfo.split("/")[1];
}

/*
 * ---------------------------------------------------------
 * 특정 시간대 예약 여부 확인
 * ---------------------------------------------------------
 *
 * true  : 예약 존재
 * false : 예약 없음
 * ---------------------------------------------------------
 */
public boolean isReserved(int slotIndex) {

    return slots.containsKey(slotIndex);
}

/*
 * ---------------------------------------------------------
 * 전체 예약 정보 반환
 * ---------------------------------------------------------
 *
 * 프로그램 종료 시
 * CSV 파일 저장을 위해 사용
 *
 * LaundrySystem 클래스에서 호출
 * ---------------------------------------------------------
 */
public Map<Integer, String> getSlots() {

    return slots;
}


}
