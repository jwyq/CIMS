package com.cims.backend.controller.reference;

import com.cims.backend.dto.ApiResponse;
import com.cims.backend.dto.reference.DictEntryResponse;
import com.cims.backend.dto.reference.OrgOptionResponse;
import com.cims.backend.service.reference.ReferenceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 通用参考数据：字典 code→中文、机构列表（存 code/id，前端展示名称）。
 */
@Validated
@RestController
@RequestMapping("/api/reference")
public class ReferenceController {

    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/dicts")
    public ApiResponse<List<DictEntryResponse>> dicts(@RequestParam @NotBlank String type) {
        return ApiResponse.success(referenceService.listDictEntries(type));
    }

    @GetMapping("/orgs")
    public ApiResponse<List<OrgOptionResponse>> orgs() {
        return ApiResponse.success(referenceService.listOrgs());
    }
}
