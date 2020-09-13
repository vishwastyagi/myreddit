package com.mycompany.redditclone.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Hashtable;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    private final TemplateEngine templateEngine;

    // Takes email message, as input, that we want to sent to user
    String build(String message){
        Context context=new Context();

        // Set message in thymeleaf context object
        context.setVariable("message",message);

        // We pass html file name and context to the template engine. At runtime thymeleaf will add email message to our html template
        return templateEngine.process("mailTemplate",context);
    }
}
