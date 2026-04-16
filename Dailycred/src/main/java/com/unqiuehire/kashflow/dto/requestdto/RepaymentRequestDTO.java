package com.unqiuehire.kashflow.dto.requestdto;

import com.unqiuehire.kashflow.constant.PaymentMode;
import lombok.Data;

@Data
public class RepaymentRequestDTO {

    private Long loanId;
    private Long loanApplicationId;
    private Double amountPaid;
    private PaymentMode paymentMode;
}