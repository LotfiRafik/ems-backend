package net.lotfi.ems.dto;

public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private int availableLeaveDays;

    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getAvailableLeaveDays() {
        return availableLeaveDays;
    }

    public void setAvailableLeaveDays(int availableLeaveDays) {
        this.availableLeaveDays = availableLeaveDays;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
