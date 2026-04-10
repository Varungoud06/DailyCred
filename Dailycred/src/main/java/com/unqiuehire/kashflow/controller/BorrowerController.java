package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.BorrowerRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.BorrowerResponseDto;
import com.unqiuehire.kashflow.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    public ApiResponse<BorrowerResponseDto> createLender(@RequestBody BorrowerRequestDto borrowerRequestDto) {
        return borrowerService.createBorrower(borrowerRequestDto);
    }

    @GetMapping("/{borrowerId}")
    public ApiResponse<BorrowerResponseDto> getLenderById(@PathVariable Long borrowerId) {
        return borrowerService.getBorrowerById(borrowerId);
    }

    @GetMapping
    public ApiResponse<List<BorrowerResponseDto>> getAllBorrowers() {
        return borrowerService.getAllBorrowers();
    }

    @PutMapping("/{borrowerId}")
    public ApiResponse<BorrowerResponseDto> updateLender(@PathVariable Long borrowerId,
                                                         @RequestBody BorrowerRequestDto borrowerRequestDto) {
        return borrowerService.updateBorrower(borrowerId, borrowerRequestDto);
    }

    @DeleteMapping("/{borrowerId}")
    public ApiResponse<String> deleteBorrower(@PathVariable Long borrowerId) {
        return borrowerService.deleteBorrower(borrowerId);
    }
}
