package com.noenv.subfolders;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

    RouterBuilder.create(vertx, "webroot/swagger.yaml")
      .onSuccess(rb -> {
        // openapi routing
        rb.operation("admin").handler(sendResponse("admin operation"));
        rb.operation("test").handler(sendResponse("test operation"));
        final Router router = Router.router(vertx);
        router.mountSubRouter("/", rb.createRouter());

        // custom router for /admin route
        // NOTE: problem only occurs when this router is added
        final Router customAdminRouter = Router.router(vertx);
        customAdminRouter.route("/foo").handler(sendResponse("foo operation"));
        router.mountSubRouter("/admin", customAdminRouter);

        // static handler
        router.route().last().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port http://localhost:8888");
          } else {
            startPromise.fail(http.cause());
          }
        });
      })
      .onFailure(System.out::println);

  }

  private Handler<RoutingContext> sendResponse(String response) {
    return ctx -> {
      final HttpServerResponse r = ctx.response();
      r.putHeader("content-type", "text/plain");
      r.end(response);
    };
  }

}
