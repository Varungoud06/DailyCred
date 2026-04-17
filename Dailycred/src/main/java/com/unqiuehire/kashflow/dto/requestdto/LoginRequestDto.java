package com.unqiuehire.kashflow.dto.requestdto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String phoneNumber;
    private String password;
}