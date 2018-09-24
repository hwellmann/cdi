package org.axonframework.cdi.messaging.annotation;

import javax.enterprise.inject.Instance;

import org.axonframework.messaging.Message;
import org.axonframework.messaging.annotation.ParameterResolver;

public class CdiParameterResolver implements ParameterResolver<Object> {

    private final Instance<?> instance;

    public CdiParameterResolver(Instance<?> instance) {
        this.instance = instance;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object resolveParameterValue(final Message message) {
        return instance.get();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean matches(final Message message) {
        return true;
    }
}
