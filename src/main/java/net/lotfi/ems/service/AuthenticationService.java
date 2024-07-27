package net.lotfi.ems.service;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LoginUserDto;
import net.lotfi.ems.dto.RegisterUserDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Role;
import net.lotfi.ems.enums.RoleEnum;
import net.lotfi.ems.mapper.EmployeeMapper;
import net.lotfi.ems.repository.EmployeeRepository;
import net.lotfi.ems.repository.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            EmployeeRepository employeeRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Employee signup(RegisterUserDto input) {
        Employee employee = new Employee();

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.EMPLOYEE);

        if (optionalRole.isEmpty()) {
            return null;
        }

        employee.setFirstName(input.getFirstName())
                .setLastName(input.getLastName())
                .setEmail(input.getEmail())
                .setRole(optionalRole.get())
                .setPassword(passwordEncoder.encode(input.getPassword()));

        return employeeRepository.save(employee);
    }

    public Employee authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        // TODO optimize
        return employeeRepository.findByEmail(input.getUsername())
                .orElseThrow();
    }
}