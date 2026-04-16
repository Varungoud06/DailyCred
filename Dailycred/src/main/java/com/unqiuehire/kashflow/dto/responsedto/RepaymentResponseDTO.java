package com.unqiuehire.kashflow.dto.responsedto;

import com.unqiuehire.kashflow.constant.PaymentMode;
import com.unqiuehire.kashflow.constant.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RepaymentResponseDTO {

    private Long id;
    private Double amountPaid;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String transactionReference;
}