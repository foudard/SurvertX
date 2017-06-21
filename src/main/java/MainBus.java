import io.vertx.core.AbstractVerticle;

/**
 * Created by Val on 21/06/2017.
 */
public class MainBus extends AbstractVerticle {
    public void start(){
        vertx.eventBus().consumer(TEST_BUS, x -> {
            x.reply(String.format("Hello %s, my name is MainBus",x.body().toString()));
        });

        vertx.eventBus().publish("TEST_PUBLISH","Hello world !!");

    }
}
