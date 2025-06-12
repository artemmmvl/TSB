package com.example.tsb.configs;
import jakarta.mail.Message;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class EmailSender {
    private final JavaMailSender javaMailSender;
    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;

    }
    @Async
    public void sendMessage(String to, String text, String subject){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}
