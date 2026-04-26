package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.dto.responsedto.RiskResultResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.Repayment;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.repository.RepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiskAnalysisServiceImpl {

    private final BorrowerRepository borrowerRepo;
    private final LoanRepository loanRepo;
    private final RepaymentRepository repaymentRepo;
//    private final RiskScoreRepository scoreRepo;

    public RiskResultResponseDto calculateRisk(Long borrowerId) {

        Borrower borrower = borrowerRepo.findById(borrowerId)
                .orElseThrow();

        List<Loan> loans = loanRepo.findByBorrowerId(borrowerId);
        List<Repayment> repayments = repaymentRepo.findByLoanBorrowerId(borrowerId);

        if (repayments.isEmpty()) {
            return new RiskResultResponseDto(650, "MEDIUM", Map.of(), List.of("New borrower"));
        }

        double missedScore = missedPaymentsScore(repayments);
        double delayScore = delaySeverityScore(repayments);
        double partialScore = partialPaymentScore(repayments);
        double earlyBonus = earlyPaymentBonus(repayments);
        double penaltyScore = penaltyImpactScore(repayments);
        double interestScore = interestImpactScore(repayments);
        double consistencyScore = consistencyScore(repayments);
        double defaultScore = defaultScore(loans);
        double dtiScore = dtiScore(borrower, loans);

        double total =
                missedScore * 0.30 +
                        delayScore * 0.15 +
                        partialScore * 0.10 +
                        penaltyScore * 0.10 +
                        interestScore * 0.05 +
                        consistencyScore * 0.10 +
                        defaultScore * 0.10 +
                        dtiScore * 0.10 +
                        earlyBonus; // bonus

        total = Math.min(1, total); // cap

        int score = (int) (300 + (total * 550));
        String category = getCategory(score);

        return new RiskResultResponseDto(score, category,
                Map.of(
                        "missed", missedScore,
                        "delay", delayScore,
                        "partial", partialScore,
                        "penalty", penaltyScore,
                        "interest", interestScore,
                        "consistency", consistencyScore
                ),
                generateRecommendations(score)
        );
    }

    private List<String> generateRecommendations(int score) {

        List<String> list = new ArrayList<>();

        if (score < 600) {
            list.add("Reduce missed payments");
            list.add("Avoid loan defaults");
        }
        if (score < 700) {
            list.add("Improve payment consistency");
        }
        if (score < 750) {
            list.add("Lower debt-to-income ratio");
        }

        return list;
    }

    public static String getCategory(int score) {
        if (score >= 750) return "LOW";
        if (score >= 650) return "MEDIUM";
        if (score >= 550) return "HIGH";
        return "VERY_HIGH";
    }

    private double missedPaymentsScore(List<Repayment> repayments) {

        long missedCount = repayments.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsMissedPayment()))
                .count();

        double ratio = (double) missedCount / repayments.size();

        return 1 - ratio;
    }

    private double delaySeverityScore(List<Repayment> repayments) {

        double avgDelay = repayments.stream()
                .filter(r -> r.getMissedDays() != null)
                .mapToInt(Repayment::getMissedDays)
                .average()
                .orElse(0);

        if (avgDelay == 0) return 1;

        return Math.max(0, 1 - (avgDelay / 30.0)); // 30 days worst
    }

    private double partialPaymentScore(List<Repayment> repayments) {

        long partial = repayments.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsPartialPayment()))
                .count();

        return 1 - ((double) partial / repayments.size());
    }

    private double earlyPaymentBonus(List<Repayment> repayments) {

        long early = repayments.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsEarlyPayment()))
                .count();

        double ratio = (double) early / repayments.size();

        return ratio * 0.05; // max +5%
    }

    private double penaltyImpactScore(List<Repayment> repayments) {

        double totalPenalty = repayments.stream()
                .mapToDouble(r -> r.getPenaltyAmount() != null ? r.getPenaltyAmount() : 0)
                .sum();

        if (totalPenalty == 0) return 1;

        return Math.max(0, 1 - (totalPenalty / 10000)); // normalize
    }

    private double interestImpactScore(List<Repayment> repayments) {

        double totalInterest = repayments.stream()
                .mapToDouble(r -> r.getInterestAdded() != null ? r.getInterestAdded() : 0)
                .sum();

        if (totalInterest == 0) return 1;

        return Math.max(0, 1 - (totalInterest / 50000));
    }

    private double consistencyScore(List<Repayment> repayments) {

        long onTime = repayments.stream()
                .filter(r -> r.getPaymentDate() != null && r.getMissedDays() != null && r.getMissedDays() == 0)
                .count();

        return (double) onTime / repayments.size();
    }

    private double defaultScore(List<Loan> loans) {

        long defaults = loans.stream()
                .filter(l -> l.getIsClosed().equals("DEFAULTED"))
                .count();

        return defaults == 0 ? 1 : 0;
    }

    private double dtiScore(Borrower borrower, List<Loan> loans) {

        double totalDebt = loans.stream()
                .filter(l -> l.getIsClosed().equals("ACTIVE"))
                .mapToDouble(Loan::getSanctionedAmount)
                .sum();

//        double ratio = totalDebt / borrower.getIncome();

        double ratio = totalDebt / 5000000;

        if (ratio < 0.3) return 1;
        if (ratio < 0.6) return 0.7;
        return 0.3;
    }
}
