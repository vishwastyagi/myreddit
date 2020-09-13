package com.mycompany.redditclone.service;

import com.mycompany.redditclone.exception.SpringRedditException;
import com.mycompany.redditclone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j // available from lombok, which will create slf4j object and inject to our class
public class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async // TO run this code asynchronously
    // Alternative way is to use rabbit mq or active mq. Recommended for larger scale application
    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            // Here we are creating object of MimeMessageHelper inside the lambda, which create instance of type MimeMessagePreparator
            MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
            // We are hardcoding  email address here, this can any fake email address you like
            // Here we are using fake smtp server so it does not matter, if you want to use real world smtp server like gmail etc.
            // then you have to give real email address here or it won't work
            messageHelper.setFrom("hearvishwas@gmail.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            //mailContentBuilder.build returns message in html format
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try{
            mailSender.send(messagePreparator);
            log.info("Activation email sent");
        }catch(MailException e){
            // Exceptions are common in code. Whenever those exception occurs we don't want to expose technical error/information
            // to the user e.g. IllegalStateException, NullPointerException, SocketException. We should ideally present
            // this information in understandable format
            // We do that by creating custom exception and pass in our own custom message
            // We can either create one exception class and reuse it everywhere in code or multiple exception for each use case.
            throw new SpringRedditException("Exception occurred when sending mail to " + notificationEmail.getRecipient(), e);
        }
    }
}
