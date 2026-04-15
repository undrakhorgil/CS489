package edu.miu.cs.cs489.lab7b.welcome.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphiqlForwardController {

    @GetMapping({"/graphiql", "/graphiql/"})
    public String graphiql() {
        return "forward:/graphiql/index.html";
    }
}

