package com.powercms.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Nested
  class OfFactory {
    @Test
    void createsErrorWithoutDetails() {
      ApiError err = ApiError.of("E1", "boom", "trace-1");

      assertThat(err.code()).isEqualTo("E1");
      assertThat(err.message()).isEqualTo("boom");
      assertThat(err.traceId()).isEqualTo("trace-1");
      assertThat(err.details()).isEmpty();
    }

    @Test
    void createsErrorWithNullTraceId() {
      ApiError err = ApiError.of("E1", "boom", null);

      assertThat(err.traceId()).isNull();
      assertThat(err.details()).isEmpty();
    }
  }

  @Nested
  class FieldErrorRecord {
    @Test
    void exposesFieldAndReason() {
      ApiError.FieldError fe = new ApiError.FieldError("username", "must not be blank");

      assertThat(fe.field()).isEqualTo("username");
      assertThat(fe.reason()).isEqualTo("must not be blank");
    }

    @Test
    void allowsNullValues() {
      ApiError.FieldError fe = new ApiError.FieldError(null, null);

      assertThat(fe.field()).isNull();
      assertThat(fe.reason()).isNull();
    }
  }

  @Nested
  class FullRecordConstructor {
    @Test
    void preservesAllFields() {
      List<ApiError.FieldError> details = List.of(
          new ApiError.FieldError("a", "x"),
          new ApiError.FieldError("b", "y"));
      ApiError err = new ApiError("E2", "details matter", "trace-2", details);

      assertThat(err.code()).isEqualTo("E2");
      assertThat(err.message()).isEqualTo("details matter");
      assertThat(err.traceId()).isEqualTo("trace-2");
      assertThat(err.details()).hasSize(2).containsExactlyElementsOf(details);
    }
  }
}
