package com.unqiuehire.kashflow.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerRequestDto {

    private String borrowerName;
    private String dateOfBirth;
    private String password;
    private Boolean isActive;
    private String phoneNumber;
    private String pincode;
    private String address;
    private String aadharCardNumber;
    private String panCardNumber;
}
