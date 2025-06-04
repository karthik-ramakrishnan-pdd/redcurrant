package com.pdd.redcurrant.domain.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdd.redcurrant.domain.annotations.Partner;
import com.pdd.redcurrant.domain.constants.PartnerConstants;
import com.pdd.redcurrant.domain.data.request.RequestDto;
import com.pdd.redcurrant.domain.data.response.BaseResponseDto;
import com.pdd.redcurrant.domain.data.response.PreSendTxnResponseDto;
import com.pdd.redcurrant.domain.data.response.SendTxnResponseDto;
import com.pdd.redcurrant.domain.exception.BusinessException;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
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
 * <li>A mocked {@link GCashServicePort} bean registered with a custom {@code @Partner}
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
    private GCashServicePort gcashServicePort;

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

        String json = MapperUtils.toString(request);
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
     * Tests that invoking a method on an unknown partner name returns a BaseResponseDto
     * with INVALID_ROUTING_KEY error details.
     */
    @Test
    void testUnknownPartner_ReturnsInvalidRoutingKeyResponse() {
        BaseResponseDto response = registry.invoke("INVALID_PARTNER", "sendTxn", "{}");

        Assertions.assertEquals(BusinessException.INVALID_ROUTING_KEY.getStatusCode(), response.getStatusCode());
        Assertions.assertEquals(BusinessException.INVALID_ROUTING_KEY.getReturnCode(), response.getReturnCode());
        Assertions.assertTrue(response.getReturnDescription().contains("No service registered for partner"));
    }

    /**
     * Tests that invoking a non-existent method on a valid partner returns a
     * BaseResponseDto with INVALID_METHOD error details.
     */
    @Test
    void testInvalidMethodName_ReturnsInvalidMethodResponse() {
        BaseResponseDto response = registry.invoke(PartnerConstants.PARTNER_GCASH, "nonExistentMethod", "{}");

        Assertions.assertEquals(BusinessException.INVALID_METHOD.getStatusCode(), response.getStatusCode());
        Assertions.assertEquals(BusinessException.INVALID_METHOD.getReturnCode(), response.getReturnCode());
        Assertions.assertTrue(response.getReturnDescription().contains("No suitable method named"));
    }

    /**
     * Tests that if the invoked partner service method throws a runtime exception, the
     * ServiceRegistry returns a BaseResponseDto with INTERNAL_ERROR details.
     */
    @Test
    void testServiceThrowsException_ReturnsInternalErrorResponse() {
        RequestDto request = RequestDto.builder().build();
        Mockito.when(gcashServicePort.sendTxn(ArgumentMatchers.any())).thenThrow(new RuntimeException("Service error"));

        String json = MapperUtils.toString(request);

        BaseResponseDto response = registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json);

        Assertions.assertEquals(BusinessException.INTERNAL_ERROR.getStatusCode(), response.getStatusCode());
        Assertions.assertEquals(BusinessException.INTERNAL_ERROR.getReturnCode(), response.getReturnCode());
        Assertions.assertTrue(response.getReturnDescription().contains("Error invoking method"));
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

        String json = MapperUtils.toString(request);

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

        String json1 = MapperUtils.toString(req1);
        String json2 = MapperUtils.toString(req2);

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json1);
        int initialCacheSize = registry.getMethodCacheSize();

        registry.invoke(PartnerConstants.PARTNER_GCASH, "sendTxn", json2);
        Assertions.assertEquals(initialCacheSize, registry.getMethodCacheSize());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        protected ServiceRegistry servicePortRegistry(ApplicationContext context) {
            return new ServiceRegistry(context);
        }

        @Bean
        @Partner(PartnerConstants.PARTNER_GCASH)
        protected GCashServicePort gcashServicePort() {
            return Mockito.mock(GCashServicePort.class);
        }

    }

}
