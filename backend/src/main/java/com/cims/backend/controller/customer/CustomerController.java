package com.cims.backend.controller.customer;

import com.cims.backend.domain.customer.Customer;
import com.cims.backend.domain.customer.CustomerBasicInfoHistory;
import com.cims.backend.dto.ApiResponse;
import com.cims.backend.domain.UserAccount;
import com.cims.backend.dto.CustomerBasicInfoUpdateRequest;
import com.cims.backend.dto.CustomerRegisterRequest;
import com.cims.backend.repository.UserRepository;
import com.cims.backend.service.customer.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    public CustomerController(CustomerService customerService, UserRepository userRepository) {
        this.customerService = customerService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ApiResponse<List<Customer>> queryCustomers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String customerNo,
        @RequestParam(required = false) String idType,
        @RequestParam(required = false) String idNo
    ) {
        return ApiResponse.success(customerService.queryCustomers(name, customerNo, idType, idNo));
    }

    @GetMapping("/{customerId}")
    public ApiResponse<Customer> customerDetail(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.getCustomerDetail(customerId));
    }

    @PostMapping("/register")
    public ApiResponse<Customer> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        Long operatorId = currentUserId();
        return ApiResponse.success(customerService.registerCustomer(request, operatorId));
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isEmpty()) {
            throw new IllegalStateException("Not authenticated");
        }
        return userRepository.findByUsername(auth.getName())
            .map(UserAccount::getUserId)
            .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @PutMapping("/{customerId}/basic-info")
    public ApiResponse<Customer> updateBasicInfo(@PathVariable Long customerId, @RequestBody CustomerBasicInfoUpdateRequest request) {
        return ApiResponse.success(customerService.updateBasicInfo(customerId, request, 1L));
    }

    @GetMapping("/{customerId}/basic-info-history")
    public ApiResponse<List<CustomerBasicInfoHistory>> basicInfoHistory(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.queryBasicInfoHistory(customerId));
    }
}
