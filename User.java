package dormmate;

public class User {
    private String id;
    private String name;
    private String gender; // "남" 또는 "여" 저장
    private Preference preference;

    public User(String id, String name, String gender, Preference preference) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.preference = preference;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; } // 성별 getter 추가
    public Preference getPreference() { return preference; }
}