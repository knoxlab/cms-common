package com.powercms.common.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

  @Nested
  class TwoArgConstructor {
    @Test
    void exposesCodeAndMessageWithEmptyDetails() {
      BusinessException ex = new BusinessException("E1", "boom");

      assertThat(ex.getCode()).isEqualTo("E1");
      assertThat(ex.getMessage()).isEqualTo("boom");
      assertThat(ex.getDetails()).isEmpty();
    }

    @Test
    void isRuntimeException() {
      BusinessException ex = new BusinessException("E1", "boom");

      assertThat(ex).isInstanceOf(RuntimeException.class);
    }
  }

  @Nested
  class ThreeArgConstructor {
    @Test
    void copiesDetailsListToImmutableView() {
      List<ApiError.FieldError> input = List.of(
          new ApiError.FieldError("a", "x"));
      BusinessException ex = new BusinessException("E2", "details matter", input);

      assertThat(ex.getDetails()).hasSize(1).containsExactlyElementsOf(input);
    }

    @Test
    void emptyDetailsIsOk() {
      BusinessException ex = new BusinessException("E2", "details matter", List.of());

      assertThat(ex.getDetails()).isEmpty();
    }

    @Test
    void nullDetailsIsRejected() {
      // 实际契约：List.copyOf(null) 抛 NPE，调用方必须显式传 List.of()。
      assertThatThrownBy(() -> new BusinessException("E2", "msg", null))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    void detailsListIsImmutable() {
      List<ApiError.FieldError> input = List.of(
          new ApiError.FieldError("a", "x"));
      BusinessException ex = new BusinessException("E2", "msg", input);

      assertThatThrownBy(() -> ex.getDetails().add(new ApiError.FieldError("b", "y")))
          .isInstanceOf(UnsupportedOperationException.class);
    }
  }

  @Nested
  class Throwability {
    @Test
    void canBeThrownAndCaught() {
      assertThatThrownBy(() -> {
        throw new BusinessException("E3", "kaboom");
      })
          .isInstanceOf(BusinessException.class)
          .hasMessage("kaboom")
          .extracting("code")
          .isEqualTo("E3");
    }
  }
}
