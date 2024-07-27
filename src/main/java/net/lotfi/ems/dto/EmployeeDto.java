package net.lotfi.ems.dto;

import jakarta.persistence.Column;
import net.lotfi.ems.entity.Role;
import net.lotfi.ems.enums.RoleEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private int availableLeaveDays;
    private String password;
    private Date createdAt;
    private Date updatedAt;
    private Role role;
    private Long managerId;
    private Map<String, Boolean> isSettedAttributes = new HashMap<>();

    public EmployeeDto() {
        // Iterate through all class attributes
        iterateAttributes();
    }

    public EmployeeDto(Long id, String firstName, String lastName, String email) {
        this();
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
    }

    // ********** METHODS ************************

    private void iterateAttributes() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            isSettedAttributes.put(field.getName(), false);
        }
    }

    // ************************** GETTERS *******************************
    public int getAvailableLeaveDays() {
        return availableLeaveDays;
    }

    public Long getManagerId() {
        return managerId;
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

    public String getPassword() {
        return password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public Map<String, Boolean> getIsSettedAttributes() {
        return isSettedAttributes;
    }

    // ************************** SETTERS *******************************
    public EmployeeDto setAvailableLeaveDays(int availableLeaveDays) {
        this.availableLeaveDays = availableLeaveDays;
        return this;
    }

    public EmployeeDto setId(Long id) {
        isSettedAttributes.put("id", true);
        this.id = id;
        return this;
    }

    public EmployeeDto setFirstName(String firstName) {
        isSettedAttributes.put("firstName", true);
        this.firstName = firstName;
        return this;
    }

    public EmployeeDto setLastName(String lastName) {
        isSettedAttributes.put("lastName", true);
        this.lastName = lastName;
        return this;
    }

    public EmployeeDto setManagerId(Long manager_id) {
        isSettedAttributes.put("managerId", true);
        this.managerId = manager_id;
        return this;
    }

    public EmployeeDto setEmail(String email) {
        isSettedAttributes.put("email", true);
        this.email = email;
        return this;
    }

    public EmployeeDto setPassword(String password) {
        isSettedAttributes.put("password", true);
        this.password = password;
        return this;
    }

    public EmployeeDto setCreatedAt(Date createdAt) {
        isSettedAttributes.put("createdAt", true);
        this.createdAt = createdAt;
        return this;
    }

    public EmployeeDto setUpdatedAt(Date updatedAt) {
        isSettedAttributes.put("updatedAt", true);
        this.updatedAt = updatedAt;
        return this;
    }

    public EmployeeDto setRole(Role role) {
        isSettedAttributes.put("role", true);
        this.role = role;
        return this;
    }

}
