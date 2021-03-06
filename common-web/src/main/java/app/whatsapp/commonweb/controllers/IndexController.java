package app.whatsapp.commonweb.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Value("<h1>${spring.application.name:Hello World!!}</h1>")
    private String applicationName;

    @GetMapping(value = {"/"})
    public String index() {
        return applicationName;
    }

}
