package org.axonframework.cdi.messaging.annotation;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.axonframework.common.Priority;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parameter resolver factory for instantiating Axon artifacts inside of a CDI
 * context.
 *
 * @author Simon Zambrovski
 */
@Priority(Priority.LOW)
public class CdiParameterResolverFactory implements ParameterResolverFactory {

    private final static Logger logger = LoggerFactory.getLogger(
            MethodHandles.lookup().lookupClass());

    private final BeanManager beanManager;

    public CdiParameterResolverFactory() {
        this.beanManager = CDI.current().getBeanManager();
    }

    @Override
    public ParameterResolver<?> createInstance(final Executable executable,
            final Parameter[] parameters, final int parameterIndex) {
        final Parameter parameter = parameters[parameterIndex];

        if (this.beanManager == null) {
            logger.error(
                    "BeanManager was null. This is a fatal error, an instance of {} {} is not created.",
                    parameter.getType(),
                    parameter.getAnnotations());
            return null;
        }

        logger.trace("Create instance for {} {}.", parameter.getType(), parameter.getAnnotations());

        Instance<?> instance = beanManager.createInstance().select(parameter.getType(), parameter.getAnnotations());

        if (instance.isUnsatisfied()) {
            return null;
        } else if (instance.isAmbiguous()) {
            logger.warn("Ambiguous reference for parameter type {} with qualifiers {}.",
                parameter.getType().getName(), parameter.getAnnotations());
            return null;
        } else {
            return new CdiParameterResolver(instance);
        }
    }
}
