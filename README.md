# Axon Framework CDI Support

**This is a private fork and a proof of concept to simplify and improve CDI support, using CDI 2.0.**


This [Axon Framework](https://axoniq.io) module provides support for the [CDI](http://cdi-spec.org) programming model. It is a CDI portable extension integrating the Axon Framework and providing some intelligent defaults while still allowing for configuring overrides.

The current minimum supported versions are:

* Axon Framework 3.3.5
* CDI 2.0/Java EE 8
* Java SE 8
 
Tested on WildFly 14.0.0.Final.

## Usage

The artifact is not yet released to Maven Central and you have to build it locally for the time being. Once you have built the artifact locally, simply add the following dependency:

      <dependency>
        <groupId>org.axonframework</groupId>
        <artifactId>axon-cdi20</artifactId>
        <version>0.1-SNAPSHOT</version>
      </dependency>

### Automatic Configuration
The base Axon Framework is extremely powerful and flexible. What this extension does is to provide a number of sensible defaults for Axon applications while still allowing you reasonable configuration flexibility - including the ability to override defaults. As soon as you include the module in your project, you will be able to inject a number of Axon APIs into your code using CDI. These APIs represent the most important Axon Framework building blocks:

* [CommandBus](http://www.axonframework.org/apidocs/3.3/org/axonframework/commandhandling/CommandBus.html)
* [CommandGateway](http://www.axonframework.org/apidocs/3.3/org/axonframework/commandhandling/gateway/CommandGateway.html)
* [EventBus](http://www.axonframework.org/apidocs/3.3/org/axonframework/eventhandling/EventBus.html)
* [Configuration](http://www.axonframework.org/apidocs/3.3/org/axonframework/config/Configuration.html)
 
### Overrides
You can provide configuration overrides for the following Axon artifacts by creating CDI producers for them:
* [EntityManagerProvider](http://www.axonframework.org/apidocs/3.3/org/axonframework/common/jpa/EntityManagerProvider.html)
/EventStorageEngine.html)
* [TransactionManager](http://www.axonframework.org/apidocs/3.3/org/axonframework/common/transaction/TransactionManager.html) (in case of JTA, make sure this is a transaction manager that will work with JTA. For your convenience, we have provided a JtaTransactionManager that should work in most CMT and BMT situations.)
* [EventBus](http://www.axonframework.org/apidocs/3.3/org/axonframework/eventhandling/EventBus.html)
* [CommandBus](http://www.axonframework.org/apidocs/3.3/org/axonframework/commandhandling/CommandBus.html)
* [CommandGateway](http://www.axonframework.org/apidocs/3.3/org/axonframework/commandhandling/gateway/CommandGateway.html)
* [QueryGateway](http://www.axonframework.org/apidocs/3.3/org/axonframework/queryhandling/QueryGateway.html)
* [Configurer](http://www.axonframework.org/apidocs/3.3/org/axonframework/config/Configurer.html)

For more details on these objects and the Axon Framework, please consult the [Axon Framework Reference Guide](https://docs.axonframework.org).
  
### Aggregates
You can define aggregate roots by placing a simple annotation `org.axonframework.cdi.stereotype.Aggregate` on your class. It will be automatically collected by the CDI container and registered.

### Event Handlers and Query Handlers
Event handlers and query handlers must be CDI beans. They will be automatically registered with Axon for you.

## Examples
Please have a look at my fork of the [AxonBank example](https://github.com/hwellmann/axonbank-javaee/tree/cdi-2.0).

