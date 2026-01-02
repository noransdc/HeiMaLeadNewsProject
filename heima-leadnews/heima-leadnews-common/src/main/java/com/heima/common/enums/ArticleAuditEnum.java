package com.heima.common.enums;


public enum ArticleAuditEnum {


    DRAFT(0),
    SUBMITTED(1),
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

    public void setCode(int code) {
        this.code = code;
    }
}
