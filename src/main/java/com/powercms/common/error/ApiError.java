package com.powercms.common.error;

import java.util.List;

/**
 * 统一 API 错误模型（规格书 11.1）。
 * 所有对外错误响应必须包含 code、message、traceId，字段级错误放入 details。
 */
public record ApiError(String code, String message, String traceId, List<FieldError> details) {

    public record FieldError(String field, String reason) {
    }

    public static ApiError of(String code, String message, String traceId) {
        return new ApiError(code, message, traceId, List.of());
    }
}
