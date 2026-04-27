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

import java.util.*;

@Service
@RequiredArgsConstructor
public class RiskAnalysisServiceImpl {

    private final BorrowerRepository borrowerRepo;
    private final LoanRepository loanRepo;
    private final RepaymentRepository repaymentRepo;

    public RiskResultResponseDto calculateRisk(Long borrowerId) {

        Borrower borrower = borrowerRepo.findById(borrowerId)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));

        List<Loan> loans = loanRepo.findByBorrowerId(borrowerId);
        List<Repayment> repayments = repaymentRepo.findByLoanBorrowerId(borrowerId);

        if (repayments.isEmpty()) {
            return new RiskResultResponseDto(
                    50,
                    "MEDIUM_RISK",
                    "LOW_LOAN",
                    0,
                    Map.of(),
                    List.of("New borrower - insufficient history")
            );
        }

        // 🔷 Factor Scores
        double missedScore = missedPaymentsScore(repayments);
        double delayScore = delaySeverityScore(repayments);
        double partialScore = partialPaymentScore(repayments);
        double earlyBonus = earlyPaymentBonus(repayments);
        double penaltyScore = penaltyImpactScore(repayments);
        double interestScore = interestImpactScore(repayments);
        double consistencyScore = consistencyScore(repayments);
        double defaultScore = defaultScore(loans);
        double dtiScore = dtiScore(borrower, loans);

        // 🔷 Weighted Score (0–1)
        double weightedScore =
                missedScore * 0.30 +
                        delayScore * 0.20 +
                        consistencyScore * 0.20 +
                        partialScore * 0.10 +
                        penaltyScore * 0.10 +
                        interestScore * 0.10 +
                        defaultScore * 0.10 +
                        dtiScore * 0.10 +
                        earlyBonus;

        // 🔷 Convert to 0–100
        double finalScore = weightedScore * 100;

        // 🔴 Consistency Penalty
        if (consistencyScore < 0.2) {
            finalScore -= 30;
        }

        // 🔷 Clamp
        finalScore = Math.max(0, Math.min(100, finalScore));

        int score = (int) finalScore;

        // 🔷 Eligibility + Loan Amount
        String eligibility = getLoanEligibility(score);

        double income = borrower.getMonthlyIncome() != null
                ? borrower.getMonthlyIncome().doubleValue()
                : 50000;

        double eligibleAmount = getLoanAmount(score, income);

        return new RiskResultResponseDto(
                score,
                getCategory(score),
                eligibility,
                eligibleAmount,
                Map.of(
                        "missed", missedScore,
                        "delay", delayScore,
                        "partial", partialScore,
                        "penalty", penaltyScore,
                        "interest", interestScore,
                        "consistency", consistencyScore
                ),
                generateRecommendations(score, consistencyScore)
        );
    }

    // ================= BUSINESS LOGIC =================

    private String getLoanEligibility(int score) {
        if (score >= 80) return "HIGH_LOAN";
        if (score >= 60) return "MEDIUM_LOAN";
        if (score >= 40) return "LOW_LOAN";
        return "REJECT";
    }

//    private double getLoanAmount(int score, double income) {
//        if (score >= 80) return income * 10;
//        if (score >= 60) return income * 5;
//        if (score >= 40) return income * 2;
//        return 0;
//    }

    private double getLoanAmount(int score, double income) {

        double maxSystemLoan = 500000;

        double riskCap;
        if (score >= 80) riskCap = 500000;
        else if (score >= 60) riskCap = 300000;
        else if (score >= 40) riskCap = 150000;
        else return 0;

        double incomeCap = income * 8;

        return Math.min(riskCap, Math.min(incomeCap, maxSystemLoan));
    }

    public static String getCategory(int score) {
        if (score >= 80) return "LOW_RISK";
        if (score >= 60) return "MEDIUM_RISK";
        if (score >= 40) return "HIGH_RISK";
        return "VERY_HIGH_RISK";
    }

    private List<String> generateRecommendations(int score, double consistency) {

        List<String> list = new ArrayList<>();

        if (consistency < 0.5) {
            list.add("Improve payment consistency");
        }
        if (score < 60) {
            list.add("Reduce missed payments");
        }
        if (score < 50) {
            list.add("Avoid late payments");
        }
        if (score < 70) {
            list.add("Lower debt-to-income ratio");
        }

        return list;
    }

    // ================= SCORING METHODS =================

    private double missedPaymentsScore(List<Repayment> repayments) {
        long missed = repayments.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsMissedPayment()))
                .count();
        return 1 - ((double) missed / repayments.size());
    }

    private double delaySeverityScore(List<Repayment> repayments) {
        double avgDelay = repayments.stream()
                .filter(r -> r.getMissedDays() != null)
                .mapToInt(Repayment::getMissedDays)
                .average()
                .orElse(0);

        if (avgDelay == 0) return 1;
        return Math.max(0, 1 - (avgDelay / 30.0));
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
        return ((double) early / repayments.size()) * 0.05;
    }

    private double penaltyImpactScore(List<Repayment> repayments) {
        double totalPenalty = repayments.stream()
                .mapToDouble(r -> r.getPenaltyAmount() != null ? r.getPenaltyAmount() : 0)
                .sum();

        if (totalPenalty == 0) return 1;
        return Math.max(0, 1 - (totalPenalty / 10000));
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
                .filter(r -> r.getMissedDays() != null && r.getMissedDays() == 0)
                .count();
        return (double) onTime / repayments.size();
    }

    private double defaultScore(List<Loan> loans) {
        long defaults = loans.stream()
                .filter(l -> Boolean.FALSE.equals(l.getIsClosed()))
                .count();
        return defaults == 0 ? 1 : 0;
    }

    private double dtiScore(Borrower borrower, List<Loan> loans) {

        double totalDebt = loans.stream()
                .filter(l -> Boolean.FALSE.equals(l.getIsClosed()))
                .mapToDouble(Loan::getSanctionedAmount)
                .sum();

        double income = borrower.getMonthlyIncome() != null
                ? borrower.getMonthlyIncome().doubleValue()
                : 50000;

        double ratio = totalDebt / (income * 12);

        if (ratio < 0.3) return 1;
        if (ratio < 0.6) return 0.7;
        return 0.3;
    }
}