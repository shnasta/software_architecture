package org.ucu.apps;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {
    @GetMapping("/message")
    public String getMessage() {
        return "Hello from the messages service";
    }
}
