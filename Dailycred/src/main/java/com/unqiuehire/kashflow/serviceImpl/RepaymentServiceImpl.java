package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.PaymentStatus;
import com.unqiuehire.kashflow.dto.requestdto.RepaymentRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.RepaymentResponseDTO;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.entity.Repayment;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.repository.RepaymentRepository;
import com.unqiuehire.kashflow.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public RepaymentResponseDTO makePayment(RepaymentRequestDTO request) {

        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        LoanApplication loanApplication = loanApplicationRepository.findById(request.getLoanApplicationId())
                .orElseThrow(() -> new RuntimeException("Loan Application not found"));

        //  VALIDATION
        if (!loan.getLoanApplication().getId().equals(loanApplication.getId())) {
            throw new RuntimeException("Loan does not belong to given LoanApplication");
        }

        //  Update remaining amount
        double remaining = loan.getRemainingAmount() - request.getAmountPaid();

        if (remaining < 0) {
            throw new RuntimeException("Payment exceeds remaining amount");
        }

        loan.setRemainingAmount(remaining);

        if (remaining == 0) {
            loan.setLoanStatus("CLOSED");
        }

        loanRepository.save(loan);

        // Create repayment
        Repayment repayment = Repayment.builder()
                .loan(loan)
                .loanApplication(loanApplication)
                .amountPaid(request.getAmountPaid())
                .paymentDate(LocalDate.now())
                .paymentMode(request.getPaymentMode())
                .paymentStatus(PaymentStatus.SUCCESS)
                .transactionReference(UUID.randomUUID().toString())
                .build();

        repaymentRepository.save(repayment);

        return mapToDTO(repayment);
    }

    @Override
    public List<RepaymentResponseDTO> getByLoan(Long loanId) {
        return repaymentRepository.findByLoanId(loanId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RepaymentResponseDTO> getByLoanApplication(Long loanApplicationId) {
        return repaymentRepository.findByLoanApplicationId(loanApplicationId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RepaymentResponseDTO mapToDTO(Repayment r) {
        return RepaymentResponseDTO.builder()
                .id(r.getId())
                .amountPaid(r.getAmountPaid())
                .paymentDate(r.getPaymentDate())
                .paymentMode(r.getPaymentMode())
                .paymentStatus(r.getPaymentStatus())
                .transactionReference(r.getTransactionReference())
                .build();
    }
}   