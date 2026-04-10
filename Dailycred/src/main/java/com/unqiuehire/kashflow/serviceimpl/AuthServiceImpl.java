//package com.unqiuehire.kashflow.serviceimpl;
//
//import com.unqiuehire.kashflow.dto.requestdto.LoginRequest;
//import com.unqiuehire.kashflow.dto.responsedto.LoginResponse;
//import com.unqiuehire.kashflow.entity.User;
//import com.unqiuehire.kashflow.exception.InvalidPasswordException;
//import com.unqiuehire.kashflow.exception.InvalidRoleException;
//import com.unqiuehire.kashflow.exception.UserNotFoundException;
//import com.unqiuehire.kashflow.repository.UserRepository;
//import com.unqiuehire.kashflow.service.AuthService;
//import com.unqiuehire.kashflow.service.JwtService;
//
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//
//    @Override
//    public LoginResponse login(LoginRequest request, String role) {
//
//        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        if (!user.getRole().name().equals(role)) {
//            throw new InvalidRoleException("You are not allowed to login as " + role);
//        }
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new InvalidPasswordException("Invalid password");
//        }
//
//        String token = jwtService.generateToken(
//                user.getPhoneNumber(),
//                user.getRole().name()
//        );
//
//        return new LoginResponse(token, user.getRole().name());
//    }
//}