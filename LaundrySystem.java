package dormmate;

import java.util.ArrayList;
import java.util.List;

public class LaundrySystem {
    private List<LaundryMachine> machines;
    private final String[] timeSlots = {
        "09:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00"
    };

    public LaundrySystem(int machineCount) {
        machines = new ArrayList<>();
        for (int i = 1; i <= machineCount; i++) {
            machines.add(new LaundryMachine(i));
        }
    }

    public List<LaundryMachine> getMachines() { return machines; }
    public String[] getTimeSlots() { return timeSlots; }
    
    public LaundryMachine getMachine(int id) {
        return machines.get(id - 1);
    }
}