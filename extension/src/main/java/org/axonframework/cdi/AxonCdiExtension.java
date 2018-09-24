package org.axonframework.cdi;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.axonframework.cdi.eventhandling.saga.CdiResourceInjector;
import org.axonframework.cdi.stereotype.Aggregate;
import org.axonframework.cdi.stereotype.Saga;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class AxonCdiExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Class<?>> aggregates = new ArrayList<>();
    private List<Class<?>> sagas = new ArrayList<>();
    private List<Class<?>> eventHandlers = new ArrayList<>();
    private List<Class<?>> commandHandlers = new ArrayList<>();

    <T> void processAggregate(@Observes @WithAnnotations(Aggregate.class) ProcessAnnotatedType<T> pat) {
        Class<?> clazz = pat.getAnnotatedType().getJavaClass();
        aggregates.add(clazz);
    }

    <T> void processEventHandler(@Observes @WithAnnotations(EventHandler.class) ProcessAnnotatedType<T> pat) {
        Class<T> clazz = pat.getAnnotatedType().getJavaClass();
        eventHandlers.add(clazz);
    }

    <T> void processCommandHandler(@Observes @WithAnnotations(CommandHandler.class) ProcessAnnotatedType<T> pat) {
        Class<T> clazz = pat.getAnnotatedType().getJavaClass();
        commandHandlers.add(clazz);
    }

    <T> void processSaga(@Observes @WithAnnotations(Saga.class) ProcessAnnotatedType<T> pat) {
        Class<T> clazz = pat.getAnnotatedType().getJavaClass();
        sagas.add(clazz);
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        logger.debug("Creating bean of type Configuration");

        afterBeanDiscovery.addBean()
                .addType(Configuration.class)
                .scope(ApplicationScoped.class)
                .createWith(ctx -> createConfiguration(beanManager));

        aggregates.forEach(aggregate -> createRepositoryBean(afterBeanDiscovery, beanManager, aggregate));
    }

    private Configuration createConfiguration(BeanManager beanManager) {
        Instance<Object> instance = beanManager.createInstance();
        Configurer configurer = instance.select(Configurer.class).get();

        configurer.configureResourceInjector(c -> new CdiResourceInjector(beanManager));

        registerGlobalTransactionManager(configurer, instance);
        registerAggregates(configurer);
        registerSagaManagers(configurer);
        registerCommandHandlers(configurer, instance);
        registerEventHandlers(configurer, instance);

        return configurer.buildConfiguration();
    }

    private void registerGlobalTransactionManager(Configurer configurer,
        Instance<Object> instance) {
        TransactionManager tm = instance.select(TransactionManager.class).get();
        logger.debug("Registering global transaction manager {}", tm.getClass());
        configurer.configureTransactionManager(c -> tm);
    }

    private void registerAggregates(Configurer configurer) {
        for (Class<?> aggregate : aggregates) {
            configurer.configureAggregate(aggregate);
        }
    }

    private void registerSagaManagers(Configurer configurer) {
        for (Class<?> saga : sagas) {
            configurer.registerModule(SagaConfiguration.trackingSagaManager(saga));
        }
    }

    private void registerCommandHandlers(Configurer configurer, Instance<Object> instance) {
        for (Class<?> commandHandler : commandHandlers) {
            logger.debug("Registering command handler {}", commandHandler);
            configurer.registerCommandHandler(c -> instance.select(commandHandler).get());
        }
    }

    private void registerEventHandlers(Configurer configurer, Instance<Object> instance) {
        EventHandlingConfiguration ehc = instance.select(EventHandlingConfiguration.class).get();
        for (Class<?> eventHandler : eventHandlers) {
            if (!isSaga(eventHandler) && !isAggregate(eventHandler)) {
                logger.debug("Registering event handler {}", eventHandler);
                Object eh = instance.select(eventHandler).get();
                ehc.registerEventHandler(c -> eh);
            }
        }
        configurer.registerModule(ehc);
    }

    private void createRepositoryBean(AfterBeanDiscovery abd, BeanManager beanManager, Class<?> aggregate) {
        logger.debug("Creating bean of type Repository<{}>", aggregate);
        abd.addBean()
            .addType(new ParameterizedTypeImpl(Repository.class, aggregate))
            .scope(ApplicationScoped.class)
            .createWith(ctx -> createRepository(beanManager, aggregate));
    }

    private <T> Repository<T> createRepository(BeanManager beanManager, Class<T> aggregate) {
        return beanManager.createInstance().select(Configuration.class).get().repository(aggregate);
    }

    private boolean isAggregate(Class<?> clazz) {
        return aggregates.contains(clazz);
    }

    private boolean isSaga(Class<?> clazz) {
        return sagas.contains(clazz);
    }
}
