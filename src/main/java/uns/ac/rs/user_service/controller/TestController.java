package uns.ac.rs.user_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/get")
    public String getTestMessage() {
        return "Hello, this is a test message!";
    }
}
