package com.pdd.redcurrant.domain.registry;

import com.pdd.redcurrant.domain.annotations.Partner;
import com.pdd.redcurrant.domain.constants.PartnerConstants;
import com.pdd.redcurrant.domain.ports.api.ServiceRegistryPort;
import com.pdd.redcurrant.domain.utils.MapperUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ServicePortRegistry is responsible for dynamically resolving and invoking
 * partner-specific service methods at runtime. It maintains a registry of partner
 * services annotated with {@link Partner} and allows invoking methods on them using
 * reflection.
 * <p>
 * The method invocation uses a cached mechanism for performance and supports
 * deserializing JSON string input to the target DTO before invocation.
 * </p>
 *
 * This is useful for building a dynamic dispatch layer in a microservice architecture
 * where multiple partners have their own service implementations behind a unified
 * interface.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceRegistry implements ServiceRegistryPort {

    /**
     * A thread-safe map of partner names to their service implementation instances.
     */
    private final ConcurrentMap<String, Object> partnerServices = new ConcurrentHashMap<>();

    /**
     * A thread-safe cache of resolved {@link Method} objects keyed by
     * {@link MethodSignature}.
     */
    private final ConcurrentMap<MethodSignature, Method> methodCache = new ConcurrentHashMap<>();

    /**
     * Spring's application context used to discover beans annotated with {@link Partner}.
     */
    private final ApplicationContext applicationContext;

    /**
     * Initializes the registry by scanning the Spring context for beans annotated with
     * {@link Partner} and registers them using the partner name specified in the
     * annotation.
     */
    @PostConstruct
    public void initialize() {
        applicationContext.getBeansWithAnnotation(Partner.class).forEach((beanName, service) -> {
            Partner annotation = service.getClass().getAnnotation(Partner.class);
            partnerServices.put(PartnerConstants.PARTNER_GCASH, service);
        });
    }

    /**
     * Invokes the specified method on the service registered for the given partner. The
     * input is passed as a raw JSON string and deserialized to the expected parameter
     * type.
     * @param partnerName The unique identifier of the partner.
     * @param methodName The name of the method to invoke.
     * @param rawJson The JSON string representing the method parameter.
     * @param <T> The expected return type of the method.
     * @return The result of the invoked method.
     * @throws IllegalArgumentException if the partner is not found, the method is
     * invalid, or JSON deserialization fails.
     * @throws IllegalStateException if the method access is not allowed.
     * @throws RuntimeException if the method invocation throws an exception.
     */
    @Override
    public <T> T invoke(String partnerName, String methodName, String rawJson) {
        Object service = partnerServices.get(partnerName);
        if (service == null) {
            throw new IllegalArgumentException("No service registered for partner: " + partnerName);
        }

        Method method = methodCache.computeIfAbsent(new MethodSignature(service.getClass(), methodName),
                key -> findMethodByName(service.getClass(), methodName));

        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("Method " + methodName + " must have exactly one parameter.");
        }

        Class<?> paramType = method.getParameterTypes()[0];
        Object deserializedDto;

        try {
            deserializedDto = MapperUtils.convert(rawJson, paramType);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("JSON conversion failed for " + paramType.getSimpleName(), ex);
        }

        try {
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(service, deserializedDto);
            return result;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException("Access denied for method: " + methodName, ex);
        }
        catch (InvocationTargetException ex) {
            throw new RuntimeException("Invocation failed for: " + methodName, ex.getCause());
        }
    }

    /**
     * Returns the current size of the internal method cache. This is primarily used in
     * test scenarios to assert caching behavior.
     * @return The number of cached method signatures.
     */
    public int getMethodCacheSize() {
        return methodCache.size();
    }

    /**
     * Searches for a public method in the given service class by name. The method must
     * have exactly one parameter to be considered valid.
     * @param serviceClass The class in which to search for the method.
     * @param methodName The name of the method to find.
     * @return The {@link Method} object if found.
     * @throws IllegalArgumentException if no suitable method is found.
     */
    private Method findMethodByName(Class<?> serviceClass, String methodName) {
        for (Method method : serviceClass.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        throw new IllegalArgumentException(
                "No suitable method found with name: " + methodName + " in " + serviceClass.getName());
    }

    /**
     * A value object representing a unique key for method caching, based on the service
     * class and method name.
     */
    @AllArgsConstructor
    private static final class MethodSignature {

        private final Class<?> serviceClass;

        private final String methodName;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MethodSignature)) {
                return false;
            }
            MethodSignature that = (MethodSignature) o;
            return serviceClass.equals(that.serviceClass) && methodName.equals(that.methodName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceClass, methodName);
        }

    }

}
