package com.rufino.server.services.impl;

import static com.rufino.server.constant.EmailConst.*;

import java.util.Date;

import com.rufino.server.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmail(String firstName, String password, String email) {
        SimpleMailMessage msg = createPasswordMessage(firstName, password, email);
        emailSender.send(msg);
    }

    private SimpleMailMessage createPasswordMessage(String firstName, String password, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject(EMAIL_SUBJECT);
        message.setText(
                "Hello " + firstName + "\n\n Your new account password is: " + password + "\n\n The Support Team");
        message.setSentDate(new Date());
        return message;
    }

}
