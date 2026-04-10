package com.unqiuehire.kashflow.dto.requestdto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LenderRequestDto {

    private String lenderName;
    private Integer cibil;
    private String dateOfBirth;
    private String password;
    private Boolean isActive;
    private String phoneNumber;
    private String pincode;
    private String address;
}