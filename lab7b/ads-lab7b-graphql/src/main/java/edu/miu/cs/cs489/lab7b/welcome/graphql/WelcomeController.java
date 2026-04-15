package edu.miu.cs.cs489.lab7b.welcome.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WelcomeController {

    @QueryMapping
    public String welcome(@Argument String name) {
        if (name == null || name.isBlank()) {
            return "Welcome world!";
        }
        return "Welcome " + name.trim() + "!";
    }
}

