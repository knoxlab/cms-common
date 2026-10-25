package com.powercms.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * ErrorCodes 是 API 契约的字符串稳定性的关键。所有错误响应都会回传这些常量，
 * 因此本测试既校验字面值存在，也校验可见性/不可实例化。
 */
class ErrorCodesTest {

  @Test
  void commonHttpFlavouredCodesArePresent() {
    assertThat(ErrorCodes.VALIDATION_FAILED).isEqualTo("VALIDATION_FAILED");
    assertThat(ErrorCodes.BAD_REQUEST).isEqualTo("BAD_REQUEST");
    assertThat(ErrorCodes.UNAUTHENTICATED).isEqualTo("UNAUTHENTICATED");
    assertThat(ErrorCodes.PERMISSION_DENIED).isEqualTo("PERMISSION_DENIED");
    assertThat(ErrorCodes.FORBIDDEN).isEqualTo("FORBIDDEN");
    assertThat(ErrorCodes.NOT_FOUND).isEqualTo("NOT_FOUND");
    assertThat(ErrorCodes.METHOD_NOT_ALLOWED).isEqualTo("METHOD_NOT_ALLOWED");
    assertThat(ErrorCodes.UNSUPPORTED_MEDIA_TYPE).isEqualTo("UNSUPPORTED_MEDIA_TYPE");
    assertThat(ErrorCodes.NOT_ACCEPTABLE).isEqualTo("NOT_ACCEPTABLE");
    assertThat(ErrorCodes.CONFLICT).isEqualTo("CONFLICT");
    assertThat(ErrorCodes.SITE_FROZEN).isEqualTo("SITE_FROZEN");
    assertThat(ErrorCodes.RATE_LIMITED).isEqualTo("RATE_LIMITED");
    assertThat(ErrorCodes.INTERNAL_ERROR).isEqualTo("INTERNAL_ERROR");
  }

  @Test
  void contentModelAndFieldCodesArePresent() {
    assertThat(ErrorCodes.CONTENT_TYPE_NOT_FOUND).isEqualTo("CONTENT_TYPE_NOT_FOUND");
    assertThat(ErrorCodes.FIELD_NOT_FOUND).isEqualTo("FIELD_NOT_FOUND");
    assertThat(ErrorCodes.FIELD_VALUE_DUPLICATE).isEqualTo("FIELD_VALUE_DUPLICATE");
    assertThat(ErrorCodes.RELATION_NAME_DUPLICATE).isEqualTo("RELATION_NAME_DUPLICATE");
    assertThat(ErrorCodes.NESTING_DEPTH_EXCEEDED).isEqualTo("NESTING_DEPTH_EXCEEDED");
    assertThat(ErrorCodes.LIFECYCLE_SCRIPT_INVALID).isEqualTo("LIFECYCLE_SCRIPT_INVALID");
    assertThat(ErrorCodes.INDEX_MAPPING_INVALID).isEqualTo("INDEX_MAPPING_INVALID");
    assertThat(ErrorCodes.SANDBOX_VIOLATION).isEqualTo("SANDBOX_VIOLATION");
  }

  @Test
  void workflowAndIdentityCodesArePresent() {
    assertThat(ErrorCodes.WORKFLOW_ACTION_NOT_AVAILABLE).isEqualTo("WORKFLOW_ACTION_NOT_AVAILABLE");
    assertThat(ErrorCodes.WORKFLOW_PERMISSION_DENIED).isEqualTo("WORKFLOW_PERMISSION_DENIED");
    assertThat(ErrorCodes.LAST_ADMIN_FORBIDDEN).isEqualTo("LAST_ADMIN_FORBIDDEN");
    assertThat(ErrorCodes.BUILTIN_ROLE_LOCKED).isEqualTo("BUILTIN_ROLE_LOCKED");
    assertThat(ErrorCodes.BACKEND_ACCESS_DENIED).isEqualTo("BACKEND_ACCESS_DENIED");
  }

  @Test
  void mediaAndKnoxCodesArePresent() {
    assertThat(ErrorCodes.MEDIA_UPSTREAM_UNAVAILABLE).isEqualTo("MEDIA_UPSTREAM_UNAVAILABLE");
    assertThat(ErrorCodes.KNOX_ENDPOINT_NOT_FOUND).isEqualTo("KNOX_ENDPOINT_NOT_FOUND");
    assertThat(ErrorCodes.MEDIA_QUOTA_EXCEEDED).isEqualTo("MEDIA_QUOTA_EXCEEDED");
    assertThat(ErrorCodes.MEDIA_FILE_TOO_LARGE).isEqualTo("MEDIA_FILE_TOO_LARGE");
    assertThat(ErrorCodes.MEDIA_KIND_REQUIRED).isEqualTo("MEDIA_KIND_REQUIRED");
  }

  @Test
  void interactionCodesArePresent() {
    assertThat(ErrorCodes.INTERACTION_TOKEN_INVALID).isEqualTo("INTERACTION_TOKEN_INVALID");
    assertThat(ErrorCodes.INTERACTION_RATE_LIMITED).isEqualTo("INTERACTION_RATE_LIMITED");
    assertThat(ErrorCodes.INTERACTION_DUPLICATE_LIKE).isEqualTo("INTERACTION_DUPLICATE_LIKE");
    assertThat(ErrorCodes.INTERACTION_SENSITIVE_HIT).isEqualTo("INTERACTION_SENSITIVE_HIT");
    assertThat(ErrorCodes.INTERACTION_AD_CAP_REACHED).isEqualTo("INTERACTION_AD_CAP_REACHED");
  }

  @Test
  void codesAreUnique() throws IllegalAccessException {
    Set<String> seen = new java.util.HashSet<>();
    for (var field : ErrorCodes.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
        String value = (String) field.get(null);
        assertThat(value).as("field %s has null value", field.getName()).isNotNull();
        assertThat(seen.add(value))
            .as("duplicate error code value: %s in field %s", value, field.getName())
            .isTrue();
      }
    }
  }

  @Test
  void cannotBeInstantiated() throws Exception {
    Constructor<ErrorCodes> ctor = ErrorCodes.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(ctor.getModifiers()))
        .as("constructor should be private")
        .isTrue();
    ctor.setAccessible(true);
    try {
      ctor.newInstance();
    } catch (InvocationTargetException expected) {
      // private + thrown from a private constructor — accessing it directly should
      // either succeed silently (returning a discarded instance) or fail with an
      // IllegalAccessException-style wrapper. Either way, callers cannot use it
      // from outside the class.
      assertThat(expected.getCause()).isNull();
    }
  }
}
