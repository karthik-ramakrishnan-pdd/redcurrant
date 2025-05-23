package com.pdd.redcurrant.domain.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.annotations.Partner;
import com.pdd.redcurrant.domain.constants.PartnerConstants;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.response.PreSendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.ports.api.GcashServicePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the {@link ServiceRegistry} class.
 * <p>
 * This test class uses Spring's testing support to load a minimal application context
 * with mocked partner service beans. It verifies the dynamic invocation capabilities of
 * the ServiceRegistry, including:
 * <ul>
 * <li>Happy path execution of partner service methods with JSON input
 * deserialization.</li>
 * <li>Handling of null and partial JSON arguments gracefully.</li>
 * <li>Proper error handling when unknown partners or invalid method names are
 * invoked.</li>
 * <li>Exception wrapping behavior when partner services throw runtime exceptions.</li>
 * <li>Verification of method caching behavior to optimize repeated method lookups.</li>
 * </ul>
 * <p>
 * The test relies on a {@link TestConfiguration} to provide:
 * <ul>
 * <li>A real {@link ObjectMapper} instance for JSON serialization/deserialization.</li>
 * <li>A mocked {@link GcashServicePort} bean registered with a custom {@code @Partner}
 * qualifier.</li>
 * <li>The {@link ServiceRegistry} bean that uses the Spring {@link ApplicationContext} to
 * discover and invoke partner services.</li>
 * </ul>
 * <p>
 * This test class demonstrates an integration-style unit test that exercises the
 * {@code ServiceRegistry} logic with Spring-managed beans and mocks. It balances between
 * pure unit testing and light integration testing.
 */
@ExtendWith({ SpringExtension.class })
class ServiceRegistryTest {

    @Autowired
    private ServiceRegistry registry;

    @Autowired
    private GcashServicePort gcashServicePort;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        Mockito.reset(gcashServicePort);
        registry.initialize();
    }

    // ==========================
    // 1. Happy Path Tests
    // ==========================

    /**
     * Tests that invoking a valid method ("sendTxn") on a known partner ("GCASH") with a
     * valid JSON request correctly returns the expected response. Verifies JSON
     * deserialization and method invocation flow.
     */
    @Test
    void testValidSendTxnCall_ReturnsExpectedResponse() throws JsonProcessingException {
        RequestDto request = RequestDto.builder().build();
        SendTxnResponseDto expected = SendTxnResponseDto.builder().build();

        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any(RequestDto.class))).thenReturn(expected);

        String json = objectMapper.writeValueAsString(request);
        SendTxnResponseDto actual = registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(gcashServicePort).sendTxn(ArgumentMatchers.any(RequestDto.class));
    }

    /**
     * Tests that invoking a method ("preSendTxn") with a null argument is handled
     * gracefully by the ServiceRegistry and returns the expected response.
     */
    @Test
    void testNullArgumentIsHandledGracefully() {
        PreSendTxnResponseDto expected = PreSendTxnResponseDto.builder().build();
        Mockito.when(gcashServicePort.preSendTxn(null)).thenReturn(expected);

        PreSendTxnResponseDto actual = registry.invoke(PartnerConstants.PARTNER_GCASH, "preSendTxn", null);

        Assertions.assertEquals(expected, actual);
    }

    /**
     * Tests that invoking a method ("sendTxn") with partial JSON input (missing some
     * fields) does not cause failure and returns the expected response.
     */
    @Test
    void testPartialJsonIsHandledCorrectly() {
        String json = """
                {
                  "method": "sendTxn"
                }
                """;
        SendTxnResponseDto expected = SendTxnResponseDto.builder().build();
        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any())).thenReturn(expected);

        SendTxnResponseDto actual = registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json);

        Assertions.assertEquals(expected, actual);
    }

    // ==========================
    // 2. Negative and Error Cases
    // ==========================

    /**
     * Tests that invoking a method on an unknown partner throws an
     * IllegalArgumentException with an appropriate error message.
     */
    @Test
    void testUnknownPartner_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> registry.invoke("INVALID_PARTNER", "sendTxn", "{}"));
        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).contains("No service registered for partner");
    }

    /**
     * Tests that invoking a non-existent method on a known partner throws an
     * IllegalArgumentException indicating no suitable method found.
     */
    @Test
    void testInvalidMethodName_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> registry.invoke(PartnerConstants.PARTNER_GCASH, "nonExistentMethod", "{}"));
        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).contains("No suitable method found");
    }

    /**
     * Tests that if the invoked partner service method throws a runtime exception, the
     * ServiceRegistry wraps it inside a RuntimeException with a meaningful message.
     */
    @Test
    void testServiceThrowsException_IsWrappedInRuntimeException() throws JsonProcessingException {
        RequestDto request = RequestDto.builder().build();
        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any())).thenThrow(new RuntimeException("Service error"));

        String json = objectMapper.writeValueAsString(request);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json));

        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).contains("Invocation failed");
        org.assertj.core.api.Assertions.assertThat(ex.getCause()).hasMessage("Service error");
    }

    // ==========================
    // 3. Caching Behavior
    // ==========================

    /**
     * Tests that method lookup results are cached in the ServiceRegistry, so repeated
     * calls to the same method reuse the cached Method instance. Verifies that the cache
     * size remains stable and the method is invoked twice.
     */
    @Test
    void testMethodCaching_ReusesSameMethod() throws JsonProcessingException {
        RequestDto request = RequestDto.builder().build();
        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any())).thenReturn(SendTxnResponseDto.builder().build());

        String json = objectMapper.writeValueAsString(request);

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json);
        int initialCacheSize = registry.getMethodCacheSize();

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json);
        Assertions.assertEquals(initialCacheSize, registry.getMethodCacheSize());

        Mockito.verify(gcashServicePort, Mockito.times(2)).sendTxn(ArgumentMatchers.any());
    }

    /**
     * Tests that calling the same method with different JSON arguments does not increase
     * the method cache size, ensuring method caching is based on method signature (class
     * + method name) only, not on parameters.
     */
    @Test
    void testSameMethodDifferentArgs_ReusesSameCacheEntry() throws JsonProcessingException {
        RequestDto req1 = RequestDto.builder().build();
        RequestDto req2 = RequestDto.builder().metadata(null).build();

        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any())).thenReturn(SendTxnResponseDto.builder().build());

        String json1 = objectMapper.writeValueAsString(req1);
        String json2 = objectMapper.writeValueAsString(req2);

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json1);
        int initialCacheSize = registry.getMethodCacheSize();

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json2);
        Assertions.assertEquals(initialCacheSize, registry.getMethodCacheSize());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        protected ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        protected ServiceRegistry servicePortRegistry(ApplicationContext context) {
            return new ServiceRegistry(context);
        }

        @Bean
        @Partner(PartnerConstants.PARTNER_GCASH)
        protected GcashServicePort gcashServicePort() {
            return Mockito.mock(GcashServicePort.class);
        }

    }

}
