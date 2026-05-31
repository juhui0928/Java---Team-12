package dormmate;

import java.util.HashMap;
import java.util.Map;

public class LaundryMachine {
    private int id; // 세탁기 번호
    private Map<Integer, String> slots; // 시간대 인덱스 -> 예약자 이름

    public LaundryMachine(int id) {
        this.id = id;
        this.slots = new HashMap<>();
    }

    public int getId() { return id; }
    
    // 예약하기
    public boolean reserve(int slotIndex, String userName) {
        if (slots.containsKey(slotIndex)) return false;
        slots.put(slotIndex, userName);
        return true;
    }

    // 취소하기 (본인 확인)
    public boolean cancel(int slotIndex, String userName) {
        if (userName.equals(slots.get(slotIndex))) {
            slots.remove(slotIndex);
            return true;
        }
        return false;
    }

    public String getReservedUser(int slotIndex) {
        return slots.get(slotIndex);
    }
    
    public boolean isReserved(int slotIndex) {
        return slots.containsKey(slotIndex);
    }
}
