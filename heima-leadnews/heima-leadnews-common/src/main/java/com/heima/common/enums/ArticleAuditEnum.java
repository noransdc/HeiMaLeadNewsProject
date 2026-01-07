package com.heima.common.enums;


import java.util.EnumSet;
import java.util.Set;

public enum ArticleAuditEnum {


    DRAFT(0),
    PENDING_AUDIT(1),
    AUTO_AUDIT_FAILED(2),
    MANUAL_AUDIT_FAILED(3),
    AUDIT_SUCCESS(8),
    PUBLISHED(9);


    private int code;

    ArticleAuditEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ArticleAuditEnum codeOf(int code){
        for (ArticleAuditEnum value : values()) {
            if (value.code == code){
                return value;
            }
        }
        return null;
    }

    public boolean canTransitTo(ArticleAuditEnum target){
        switch (this) {
            case DRAFT:
                // 草稿只能提交进入待审核
                return target == PENDING_AUDIT;

            case PENDING_AUDIT:
                // 待审核 → 自动审核失败 / 审核通过
                return target == AUTO_AUDIT_FAILED
                        || target == AUDIT_SUCCESS;

            case AUTO_AUDIT_FAILED:
                // 自动审核失败 → 人工审核失败 / 审核通过
                return target == MANUAL_AUDIT_FAILED
                        || target == AUDIT_SUCCESS;

            case MANUAL_AUDIT_FAILED:
                // 人工审核失败是终态（除非你未来允许重新提交）
                return false;

            case AUDIT_SUCCESS:
                // 审核通过 → 发布
                return target == PUBLISHED;

            case PUBLISHED:
                // 发布是终态
                return false;

            default:
                return false;
        }
    }

    public boolean canTransitToV1(ArticleAuditEnum target) {
        return allowedTargets().contains(target);
    }

    private Set<ArticleAuditEnum> allowedTargets() {
        switch (this) {
            case DRAFT:
                return EnumSet.of(PENDING_AUDIT);
            case PENDING_AUDIT:
                return EnumSet.of(AUTO_AUDIT_FAILED, AUDIT_SUCCESS);
            case AUTO_AUDIT_FAILED:
                return EnumSet.of(MANUAL_AUDIT_FAILED, AUDIT_SUCCESS);
            case AUDIT_SUCCESS:
                return EnumSet.of(PUBLISHED);
            default:
                return EnumSet.noneOf(ArticleAuditEnum.class);
        }
    }

}
