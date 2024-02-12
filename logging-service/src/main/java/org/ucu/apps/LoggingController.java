package org.ucu.apps;

import java.util.Map;
import java.util.UUID;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoggingController {

    Logger logger = Logger.getLogger(LoggingController.class.getName());

    private Map<UUID, String> messages = new ConcurrentHashMap<>();

    @GetMapping("/logger")
    public String getMessages() {
        return messages.values().toString();
    }

    @PostMapping("/logger")
    public ResponseEntity<Void> postMessage(@RequestBody Message message) {
        messages.put(message.getId(), message.getMessage());
        logger.info("Message received: " + message.getMessage());
        return ResponseEntity.ok().build();
    }

}
