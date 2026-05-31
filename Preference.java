package dormmate;

/**
 * 변경된 기준: 1~5 척도 및 0/1 (비흡연/흡연, 싫음/가능)
 */
public class Preference {
    private int smoking;    // 0(비흡연), 1(흡연)
    private int drinking;   // 1 ~ 5
    private int sleep;      // 1 ~ 5 (11시이전 ~ 2시이후)
    private int cleaning;   // 1 ~ 5
    private int noise;      // 1 ~ 5
    private int call;       // 1 ~ 5
    private int eating;     // 0(싫음), 1(가능)

    public Preference(int smoking, int drinking, int sleep, int cleaning, int noise, int call, int eating) {
        this.smoking = smoking;
        this.drinking = drinking;
        this.sleep = sleep;
        this.cleaning = cleaning;
        this.noise = noise;
        this.call = call;
        this.eating = eating;
    }

    public int getSmoking() { return smoking; }
    public int getDrinking() { return drinking; }
    public int getSleep() { return sleep; }
    public int getCleaning() { return cleaning; }
    public int getNoise() { return noise; }
    public int getCall() { return call; }
    public int getEating() { return eating; }
}
