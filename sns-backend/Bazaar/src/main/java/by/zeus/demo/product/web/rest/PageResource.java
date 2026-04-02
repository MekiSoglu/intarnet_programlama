package by.zeus.demo.product.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageResource {

    @GetMapping("/")
    public String index() {
        return "odev.html";
    }
}