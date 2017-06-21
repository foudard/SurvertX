import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


/**
 * Created by Val on 21/06/2017.
 */
public class MainVertx extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(5);
        vertx.deployVerticle(MainVertx.class.getCanonicalName(),options);
    }

    @Override public void start(){
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create()); // Permet de reçevoir du contenu dans le body de la requète HTTP
        router.route().handler(GetContextHeader()); // Défintion de règle

        router.get("/test").handler(x -> {
            x.response().end("Hello world -> " + this.toString());
        });

        vertx.eventBus().send(MainBus.TEST_BUS,"testbus", r -> {}) ;

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, x->{
            System.out.println("Front 1 listen -> " + x.succeeded());
        });
    }

    public static Handler<RoutingContext> GetContextHeader(){
        return (RoutingContext context) -> {
            context.response().headers().add(HttpHeaders.CONTENT_TYPE, "application/json");
            context.response().headers().add("content-type", "text/html;charset=UTF-8");

            context.response()
                    // do not allow proxies to cache the data
                    .putHeader("Cache-Control", "no-store, no-cache")
                    // prevents Internet Explorer from MIME - sniffing a
                    // response away from the declared content-type
                    .putHeader("X-Content-Type-Options", "nosniff")
                    // Strict HTTPS (for about ~6Months)
                    .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                    // IE8+ do not allow opening of attachments in the context
                    // of this resource
                    .putHeader("X-Download-Options", "noopen")
                    // enable XSS for IE
                    .putHeader("X-XSS-Protection", "1; mode=block")
                    // deny frames
                    .putHeader("X-FRAME-OPTIONS", "DENY");

            System.out.println("handle -> " + context.request().path());

            context.next();
        };
    }
}
