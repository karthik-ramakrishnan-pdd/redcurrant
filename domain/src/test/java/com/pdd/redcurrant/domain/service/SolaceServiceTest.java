package com.pdd.redcurrant.domain.service;

import com.pdd.redcurrant.domain.data.request.BaseRequestDto;
import com.pdd.redcurrant.domain.registry.ServiceRegistry;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Set;

class SolaceServiceTest {

    @InjectMocks
    private SolaceServiceImpl solaceService;

    @Mock
    private Validator validator;

    @Mock
    private ServiceRegistry serviceRegistry;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the {@link SolaceServiceImpl#process(String)} method with a valid JSON input.
     *
     * <p>
     * This test ensures that when the input JSON contains all required fields and
     * validation passes, the {@link ServiceRegistry#invoke(String, String, String)}
     * method is called with the correct arguments.
     * </p>
     * @throws Exception if static mocking fails or any unexpected behavior occurs during
     * method execution.
     */
    @Test
    void process_validMessage_invokesService() {
        String validJson = """
                {
                    "method": "someMethod",
                    "routingKey":"someKey"
                }
                """;

        BaseRequestDto dto = BaseRequestDto.builder().method("someMethod").routingKey("someKey").build();

        // Manually mock MapperUtils.convert to return the DTO (static mocking)
        try (MockedStatic<MapperUtils> utilities = Mockito.mockStatic(MapperUtils.class)) {
            utilities.when(() -> MapperUtils.convert(validJson, BaseRequestDto.class)).thenReturn(dto);

            Mockito.when(validator.validate(dto)).thenReturn(Collections.emptySet());

            solaceService.process(validJson);

            Mockito.verify(serviceRegistry, Mockito.times(1)).invoke("someKey", "someMethod", dto.toString());
        }
    }

    /**
     * Tests the {@link SolaceServiceImpl#process(String)} method with an invalid JSON
     * input.
     *
     * <p>
     * This test ensures that if the deserialized {@link BaseRequestDto} fails validation
     * (e.g., null method), an {@link IllegalArgumentException} is thrown containing a
     * detailed validation message.
     * </p>
     * @throws IllegalArgumentException if validation fails on required fields.
     * @throws Exception if static mocking fails or any unexpected behavior occurs during
     * method execution.
     */
    @Test
    void process_invalidMessage_throwsIllegalArgumentException() {
        String invalidJson = """
                {
                    "method": null,
                    "routingKey":"someKey"
                }
                """;

        BaseRequestDto dto = BaseRequestDto.builder().method(null).routingKey("someKey").build();

        @SuppressWarnings("unchecked")
        ConstraintViolation<BaseRequestDto> violation = Mockito.mock(ConstraintViolation.class);
        Path mockPath = Mockito.mock(Path.class);

        Mockito.when(mockPath.toString()).thenReturn("method");
        Mockito.when(violation.getPropertyPath()).thenReturn(mockPath);
        Mockito.when(violation.getMessage()).thenReturn("must not be null");

        // Mock static and validation
        try (MockedStatic<MapperUtils> utilities = Mockito.mockStatic(MapperUtils.class)) {
            utilities.when(() -> MapperUtils.convert(invalidJson, BaseRequestDto.class)).thenReturn(dto);

            Mockito.when(validator.validate(dto)).thenReturn(Set.of(violation));

            IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                solaceService.process(invalidJson);
            });

            Assertions.assertTrue(exception.getMessage().contains("Validation failed:"));
            Assertions.assertTrue(exception.getMessage().contains("method must not be null"));
        }
    }

}
