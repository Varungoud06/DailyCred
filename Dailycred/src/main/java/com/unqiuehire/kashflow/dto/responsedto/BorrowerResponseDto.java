package com.unqiuehire.kashflow.dto.responsedto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowerResponseDto {

    private Long borrowerId;
    private String borrowerName;
    private Integer cibil;
    private String dateOfBirth;
    private Boolean isActive;
    private String phoneNumber;
    private String pincode;
    private String address;
}
