package net.lotfi.ems.bootstrap;

import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Role;
import net.lotfi.ems.enums.RoleEnum;
import net.lotfi.ems.repository.EmployeeRepository;
import net.lotfi.ems.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;


    public RoleSeeder(RoleRepository roleRepository, EmployeeRepository employeeRepository) {
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
        // Create default admin
        this.createDefaultAdmin();
    }

    private void loadRoles() {
        // TODO refactor
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.EMPLOYEE, RoleEnum.ADMIN };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.EMPLOYEE, "Employee role",
                RoleEnum.ADMIN, "Administrator role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName)
                        .setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }

    private void createDefaultAdmin() {
        Optional<Role> optionalAdminRole = roleRepository.findByName(RoleEnum.ADMIN);
        Role adminRole = optionalAdminRole.orElseThrow(() -> new NoSuchElementException("Admin role not found"));
        List<Employee> admins = employeeRepository.findByRole(adminRole);
        if (admins.isEmpty()){
            Employee employee = new Employee();
            employee.setEmail("admin@gmail.com")
                    .setFirstName("admin")
                    .setLastName("admin")
                    .setPassword("admin")
                    .setRole(adminRole);
            Employee savedEmployee = employeeRepository.save(employee);
        }
    }

}