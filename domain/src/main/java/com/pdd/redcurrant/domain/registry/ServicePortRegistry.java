package com.pdd.redcurrant.domain.registry;

import com.pdd.redcurrant.domain.constants.PartnerConstants;
import com.pdd.redcurrant.domain.ports.api.GCashServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ServicePortRegistry {

    private final Map<String, Object> partnerServiceMapping = new HashMap<>();

    public ServicePortRegistry(ApplicationContext applicationContext) {
        partnerServiceMapping.put(PartnerConstants.PARTNER_GCASH, applicationContext.getBean(GCashServicePort.class));
    }

    public void invokeMethod(String partnerName, String methodName, Object... args) {
        Object servicePort = partnerServiceMapping.get(partnerName);
        try {
            if (servicePort != null) {
                Method method = servicePort.getClass().getMethod(methodName, toClasses(args));
                method.invoke(servicePort, args);
            }
            else {
                throw new IllegalArgumentException("No service found for name: " + partnerName);
            }
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Error while invoking Service ({} - {}). Exception {}", partnerName, methodName, ex.getMessage());
        }
    }

    private Class<?>[] toClasses(Object[] args) {
        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
    }

}
