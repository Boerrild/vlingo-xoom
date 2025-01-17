package ${packageName};

import io.vlingo.actors.Grid;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.lattice.exchange.Exchange;
import io.vlingo.xoom.exchange.ExchangeSettings;
import io.vlingo.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.lattice.exchange.ConnectionSettings;
import io.vlingo.lattice.exchange.rabbitmq.Message;
import io.vlingo.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.lattice.exchange.Covey;
import io.vlingo.symbio.store.dispatch.Dispatcher;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ExchangeBootstrap {

  private static ExchangeBootstrap instance;

  private final Dispatcher dispatcher;

  public static ExchangeBootstrap init(final Grid stage) {
    if(instance != null) {
      return instance;
    }

    ExchangeSettings.load(Settings.properties());

    <#list exchanges as exchange>
    final ConnectionSettings ${exchange.settingsName} =
                ExchangeSettings.of("${exchange.name}").mapToConnection();

    final Exchange ${exchange.variableName} =
                ExchangeFactory.fanOutInstance(${exchange.settingsName}, "${exchange.name}", true);

      <#list exchange.coveys as covey>
    ${exchange.variableName}.register(Covey.of(
        new MessageSender(${exchange.variableName}.connection()),
        ${covey.receiverInstantiation},
        ${covey.adapterInstantiation},
        ${covey.localClass}.class,
        ${covey.externalClass}.class,
        Message.class));

      </#list>
    </#list>
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        <#list exchanges as exchange>
        ${exchange.variableName}.close();
        </#list>

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));

    instance = new ExchangeBootstrap(${producerExchanges});

    return instance;
  }

  private ExchangeBootstrap(final Exchange ...exchanges) {
    <#if producerExchanges?has_content>
    this.dispatcher = new ExchangeDispatcher(exchanges);
    <#else>
    this.dispatcher = new io.vlingo.symbio.store.dispatch.NoOpDispatcher();
    </#if>
  }

  public Dispatcher dispatcher() {
    return dispatcher;
  }


}