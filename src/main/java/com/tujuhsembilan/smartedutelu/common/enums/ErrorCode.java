package com.tujuhsembilan.smartedutelu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Centralized error codes for the application.
 * Format: {DOMAIN}_{ERROR_TYPE}_{SEQUENCE}
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Errors (SE-CMN-xxx)
    SE_CMN_001("SE-CMN-001", "Invalid request parameter"),
    SE_CMN_002("SE-CMN-002", "Resource not found"),
    SE_CMN_003("SE-CMN-003", "Duplicate resource"),
    SE_CMN_004("SE-CMN-004", "Unauthorized access"),
    SE_CMN_005("SE-CMN-005", "Internal server error"),
    SE_CMN_006("SE-CMN-006", "Validation error"),
    SE_CMN_007("SE-CMN-007", "Circular reference detected"),

    // Auth & Security Errors (SE-AUT-xxx)
    SE_AUT_001("SE-AUT-001", "Invalid credentials"),
    SE_AUT_002("SE-AUT-002", "Token expired or invalid"),
    SE_AUT_003("SE-AUT-003", "Account is not active"),
    SE_AUT_004("SE-AUT-004", "Email already registered"),
    SE_AUT_005("SE-AUT-005", "Password reset token invalid or expired"),
    SE_AUT_006("SE-AUT-006", "Current password is incorrect"),

    // User Management Errors (SE-USR-xxx)
    SE_USR_001("SE-USR-001", "User not found"),
    SE_USR_002("SE-USR-002", "Cannot delete own account"),
    SE_USR_003("SE-USR-003", "Cannot change own status"),

    // Role & Permission Errors (SE-ROL-xxx / SE-PRM-xxx)
    SE_ROL_001("SE-ROL-001", "Role not found"),
    SE_ROL_002("SE-ROL-002", "Role name already exists"),
    SE_PRM_001("SE-PRM-001", "One or more permissions not found"),

    // Organization / Tenant Errors (SE-ORG-xxx)
    SE_ORG_001("SE-ORG-001", "Organization not found"),
    SE_ORG_002("SE-ORG-002", "Organization name already exists in this tenant"),
    SE_ORG_003("SE-ORG-003", "User is already a member of this organization"),

    // Tenant Errors (SE-TNT-xxx)
    SE_TNT_001("SE-TNT-001", "Tenant not found"),
    SE_TNT_002("SE-TNT-002", "Tenant domain already exists"),
    SE_TNT_003("SE-TNT-003", "User is already a member of this tenant"),

    // Exam Category Errors (SE-CAT-xxx)
    SE_CAT_001("SE-CAT-001", "Exam category not found"),
    SE_CAT_002("SE-CAT-002", "Slug already exists in this tenant"),
    SE_CAT_003("SE-CAT-003", "Cannot set a category as its own parent"),
    SE_CAT_004("SE-CAT-004", "Cannot set a descendant as parent (circular reference)"),

    // Question Bank Errors (SE-QST-xxx)
    SE_QST_001("SE-QST-001", "Question not found"),
    SE_QST_002("SE-QST-002", "Question option not found"),
    SE_QST_003("SE-QST-003", "Question attachment not found"),
    SE_QST_004("SE-QST-004", "Question folder not found"),
    SE_QST_005("SE-QST-005", "Question category not found"),
    SE_QST_006("SE-QST-006", "Question category name already exists in this tenant"),

    // Exam Management Errors (SE-EXM-xxx)
    SE_EXM_001("SE-EXM-001", "Exam not found"),
    SE_EXM_002("SE-EXM-002", "Exam is currently active and cannot be modified"),
    SE_EXM_003("SE-EXM-003", "Exam slug already exists in this tenant"),
    SE_EXM_004("SE-EXM-004", "Exam section not found"),
    SE_EXM_005("SE-EXM-005", "Exam question not found"),
    SE_EXM_006("SE-EXM-006", "Question already exists in this section"),
    SE_EXM_007("SE-EXM-007", "Exam status transition not allowed"),
    SE_EXM_008("SE-EXM-008", "Only draft exams can be modified"),

    // Scheduling & Proctoring Errors (SE-SCH-xxx)
    SE_SCH_001("SE-SCH-001", "Schedule not found"),
    SE_SCH_002("SE-SCH-002", "End time must be after start time"),
    SE_SCH_003("SE-SCH-003", "Session not found"),
    SE_SCH_004("SE-SCH-004", "Session already terminated"),
    SE_SCH_005("SE-SCH-005", "Registration not found"),
    SE_SCH_006("SE-SCH-006", "Already registered for this exam schedule"),

    // Evaluation & Grading Errors (SE-GRD-xxx)
    SE_GRD_001("SE-GRD-001", "Attempt not found"),
    SE_GRD_002("SE-GRD-002", "Answer not found"),
    SE_GRD_003("SE-GRD-003", "Attempt already finalized"),
    SE_GRD_004("SE-GRD-004", "Rubric not found"),
    SE_GRD_005("SE-GRD-005", "Rubric criteria not found"),
    SE_GRD_006("SE-GRD-006", "Result not found"),
    SE_GRD_007("SE-GRD-007", "Result already published");

    private final String code;
    private final String message;
}