package net.lotfi.ems.controller;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LoginResponse;
import net.lotfi.ems.dto.LoginUserDto;
import net.lotfi.ems.dto.RegisterUserDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Role;
import net.lotfi.ems.enums.RoleEnum;
import net.lotfi.ems.mapper.EmployeeMapper;
import net.lotfi.ems.repository.RoleRepository;
import net.lotfi.ems.service.AuthenticationService;
import net.lotfi.ems.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@CrossOrigin("*")
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final RoleRepository roleRepository;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService,
                                    RoleRepository roleRepository) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<EmployeeDto> register(@RequestBody RegisterUserDto registerUserDto) {
        Employee registeredUser = authenticationService.signup(registerUserDto);
        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(registeredUser);

        return ResponseEntity.ok(employeeDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Employee authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setId(authenticatedUser.getId());


        return ResponseEntity.ok(loginResponse);
    }
}
