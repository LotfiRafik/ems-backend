package net.lotfi.ems.entity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "employees")
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // *********************** User fields ***********************
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    // *********************** User fields ***********************

    // *********************** Employee fields ***********************
    @Column(name = "available_leave_days")
    private Integer availableLeaveDays = 25;
    // *********************** Employee fields ***********************

    // *********************** RELATIONSHIPS FIELDS ****************************
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Leave> leaves = new ArrayList<Leave>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee manager;

    // TODO replace with ManyToMany or ManyToOne
    @ManyToOne()
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;
    // *********************** RELATIONSHIPS FIELDS ****************************

    // CONSTRUCTORS
    public Employee() {
    }

    public Employee(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


    // ********************** GETTERS **************************
    public Role getRole() {
        return role;
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
    public List<Leave> getLeaves() {
        return leaves;
    }
    public Integer getAvailableLeaveDays() {
        return availableLeaveDays;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Employee getManager() {
        return manager;
    }
    // ********************** GETTERS **************************


    // ********************** SETTERS **************************
    public Employee setId(Long id) {
        this.id = id;
        return this;
    }

    public Employee setPassword(String password) {
        this.password = password;
        return this;
    }
    public Employee setEmail(String email) {
        this.email = email;
        return this;
    }
    public Employee setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    public Employee setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public Employee setAvailableLeaveDays(Integer availableLeaveDays) {
        this.availableLeaveDays = availableLeaveDays;
        return this;
    }
    public Employee setLeaves(List<Leave> leaves) {
        this.leaves = leaves;
        return this;
    }
    public Employee setRole(Role role) {
        this.role = role;
        return this;
    }

    public Employee setManager(Employee manager) {
        this.manager = manager;
        return this;
    }
    // ********************** SETTERS **************************

    // ******************** UserDetails interface overrides *************************
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
    // ******************** UserDetails interface overrides *************************
}