package org.ucu.apps;

import java.util.UUID;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class FacadeController {

    WebClient loggingWebClient = WebClient.create("http://localhost:8081");
    WebClient messagesWebClient = WebClient.create("http://localhost:8082");

    @GetMapping("/facade")
    public Mono<String> getLogsAndMessages() {

        Mono<String> cachedValues = loggingWebClient.get()
                .uri("/logger")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messages = messagesWebClient.get()
                .uri("/message")
                .retrieve()
                .bodyToMono(String.class);

        return cachedValues.zipWith(messages, (cached, message) -> cached + " -- " + message)
                .onErrorReturn("Error occurred");


    }

    @PostMapping(value = "/facade")
    public Mono<Void> postLog(@RequestBody String message) {

        var msg = new Message(UUID.randomUUID(), message);

        return loggingWebClient.post()
                .uri("/logger")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
