/*
 * Copyright 2018 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.interceptor.Interceptor;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventBus;

/**
 * @author Harald Wellmann
 *
 */
@ApplicationScoped
@Typed()
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class DefaultComponentProducer extends DefaultConfigurer {

    @Produces
    @Alternative
    @ApplicationScoped
    Configurer defaultConfigurer() {
        return DefaultConfigurer.defaultConfiguration();
    }

    @Produces
    @Alternative
    @ApplicationScoped
    EventHandlingConfiguration defaultEventHandlingConfiguration() {
        return new EventHandlingConfiguration();
    }

    @Produces
    @ApplicationScoped
    @Alternative
    @Typed(CommandGateway.class)
    public CommandGateway commandGateway(Configuration configuration) {
        return configuration.commandGateway();
    }

    @Produces
    @Typed(CommandBus.class)
    @Alternative
    @ApplicationScoped
    public CommandBus commandBus(Configuration configuration) {
        return configuration.commandBus();
    }

    @Produces
    @Typed(EventBus.class)
    @ApplicationScoped
    @Alternative
    public EventBus eventBus(Configuration configuration) {
        return configuration.eventBus();
    }

}
