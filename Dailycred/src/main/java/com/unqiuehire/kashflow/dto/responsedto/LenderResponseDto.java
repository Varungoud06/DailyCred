package com.unqiuehire.kashflow.dto.responsedto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LenderResponseDto {

    private Long lenderId;
    private String lenderName;
    private Integer cibil;
    private String dateOfBirth;
    private Boolean isActive;
    private String phoneNumber;
    private String pincode;
    private String address;
}