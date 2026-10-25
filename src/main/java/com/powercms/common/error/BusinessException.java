package com.powercms.common.error;

import java.util.List;

/**
 * 业务异常基类。由 Web 层统一转换为 {@link ApiError} 响应。
 *
 * <p>文案有两种携带方式（OBS-MS-04）：默认携带**中文原文**，Web 层按「中文原文即键」
 * 逐段翻译；运行期拼装的句子（如 {@code "站点尚未配置 " + env + " 环境的 Knox 端点"}）
 * 没有整句键，逐段重组既丢词序也易丢空白，应改用 {@link #keyed} 携带**稳定消息键 + 参数**，
 * 由目标语言的文案决定语序与空格。</p>
 */
public class BusinessException extends RuntimeException {

    private static final Object[] NO_ARGS = new Object[0];

    private final String code;
    private final List<ApiError.FieldError> details;
    /** 稳定消息键；非空时 Web 层按键 + 参数本地化，忽略中文原文。 */
    private final String messageKey;
    private final Object[] messageArgs;

    public BusinessException(String code, String message) {
        this(code, message, List.of());
    }

    public BusinessException(String code, String message, List<ApiError.FieldError> details) {
        this(code, message, details, null);
    }

    private BusinessException(String code, String message, List<ApiError.FieldError> details,
                              String messageKey, Object... messageArgs) {
        super(message);
        this.code = code;
        this.details = List.copyOf(details);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? NO_ARGS : messageArgs.clone();
    }

    /**
     * 带稳定消息键的业务异常：对外文案取 {@code messageKey} + {@code messageArgs}
     * （框架层资源包的 6 语言文案，占位符 {@code {0}}／MessageFormat），
     * {@code message} 仅作异常自身的 message 供日志与测试阅读，不参与对外输出。
     *
     * @param code        稳定错误码（决定 HTTP 状态与 {@code ApiError.code}）
     * @param message     中文原文（仅日志/测试；请保持与目标语言文案同义便于排查）
     * @param messageKey  资源包键，如 {@code error.media.env.not.configured}
     * @param messageArgs 占位符实参，按 {@code {0}}、{@code {1}} 顺序
     */
    public static BusinessException keyed(String code, String message, String messageKey,
                                          Object... messageArgs) {
        return new BusinessException(code, message, List.of(), messageKey, messageArgs);
    }

    public String getCode() {
        return code;
    }

    public List<ApiError.FieldError> getDetails() {
        return details;
    }

    /** 稳定消息键；为 {@code null} 时 Web 层回落按中文原文翻译。 */
    public String getMessageKey() {
        return messageKey;
    }

    /** 稳定消息键的占位符实参；无键时为空数组。 */
    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }
}
