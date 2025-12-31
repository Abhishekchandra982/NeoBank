package com.example.neobank.service;

import com.example.neobank.dto.UserDto;
import com.example.neobank.entity.User;
import com.example.neobank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User registerUser(UserDto userDto) {
        User user = mapToUser(userDto);
        return userRepository.save(user);
    }
    public Map<String, Object> authenticateUser(UserDto userDto) {
        Map<String, Object> authObject = new HashMap<String, Object>();
        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
         if(user == null){
             throw new UsernameNotFoundException("User not found");
         }
         authenticationManager
                 .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
         authObject.put("token", "Bearer " .concat(jwtService.generateToken(userDto.getUsername())));
         authObject.put("user" , user);

        return authObject;
    }


    private User mapToUser(UserDto dto) {
        return User.builder()
                .lastname(dto.getLastname())
                .firstName(dto.getFirstName())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .tag("neo_" + dto.getUsername())
                .dob(dto.getDob())
                .roles(List.of("USER"))
                .build();
    }

}


