package com.abt;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.abt.domain.IndexContactRequest;
import com.abt.domain.LtfClientRequest;
import com.abt.util.CustomJacksonSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

/**
 * Routes can be defined in separated classes like shown in here
 */
public class UcsCtcIntegrationRoutes {
    //#routes-class
    private final static Logger log =
            LoggerFactory.getLogger(UcsCtcIntegrationRoutes.class);
    private final ActorRef<UcsCtcIntegrationRegistry.Command> ctcIntegrationActor;
    private final Duration askTimeout;
    private final Scheduler scheduler;
    private final String url;
    private final String username;
    private final String password;

    public UcsCtcIntegrationRoutes(ActorSystem<?> system,
                                   ActorRef<UcsCtcIntegrationRegistry.Command> ctcIntegrationActor) {
        this.ctcIntegrationActor = ctcIntegrationActor;
        scheduler = system.scheduler();
        askTimeout = system.settings().config().getDuration("integration" +
                "-service.routes.ask-timeout");
        url = system.settings().config().getString("integration-service" +
                ".destination.url");
        username = system.settings().config().getString("integration-service" +
                ".destination.username");
        password = system.settings().config().getString("integration-service" +
                ".destination.password");

    }

    private CompletionStage<UcsCtcIntegrationRegistry.ActionPerformed> sendLtFMissapClients(LtfClientRequest ltfClientRequest) {
        return AskPattern.ask(ctcIntegrationActor,
                ref -> new UcsCtcIntegrationRegistry.SendLtfMissapRequests(ltfClientRequest, url, username, password, ref), askTimeout, scheduler);
    }

    private CompletionStage<UcsCtcIntegrationRegistry.ActionPerformed> sendIndexContactsRequest(IndexContactRequest indexContactRequest) {
        return AskPattern.ask(ctcIntegrationActor,
                ref -> new UcsCtcIntegrationRegistry.SendIndexContacts(indexContactRequest, url, username, password, ref), askTimeout, scheduler);
    }

    /**
     * This method creates one route (of possibly many more that will be part
     * of your Web App)
     */
    public Route ucsIntegrationRoutes() {
        return pathPrefix("send", () ->
                concat(
                        //#send-results
                        pathSuffix("-ltf-missap-clients", () ->
                                concat(
                                        post(() ->
                                                entity(
                                                        CustomJacksonSupport.customJacksonUnmarshaller(LtfClientRequest.class),
                                                        ltfClientRequest ->
                                                                onSuccess(sendLtFMissapClients(ltfClientRequest), performed -> {
                                                                    log.info(
                                                                            "Sent LTF/MISSAP: {}", performed.response());
                                                                    if (performed.response().getDescription().toLowerCase().contains("error")) {
                                                                        return complete(StatusCodes.BAD_REQUEST, performed, Jackson.marshaller());
                                                                    } else {
                                                                        return complete(StatusCodes.OK, performed, Jackson.marshaller());
                                                                    }
                                                                })
                                                )
                                        )
                                )
                        ),
                        //#send-rejections
                        pathSuffix("-index-contacts", () ->
                                concat(
                                        post(() ->
                                                entity(
                                                        Jackson.unmarshaller(IndexContactRequest.class),
                                                        indexContactRequest ->
                                                                onSuccess(sendIndexContactsRequest(indexContactRequest), performed -> {
                                                                    log.info(
                                                                            "Sent Index Contacts: {}", performed.response().getDescription());
                                                                    if (performed.response().getDescription().toLowerCase().contains("error")) {
                                                                        return complete(StatusCodes.BAD_REQUEST, performed, Jackson.marshaller());
                                                                    } else {
                                                                        return complete(StatusCodes.OK, performed, Jackson.marshaller());
                                                                    }
                                                                })
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
