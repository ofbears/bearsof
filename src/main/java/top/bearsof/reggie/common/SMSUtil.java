package top.bearsof.reggie.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

/**
 * @author bears
 * @Date 2022/09/17
 */

@Component
public class SMSUtil {
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    JavaMailSender javaMailSender;
    public void sendMailMessage(String to,String subject,String text){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        javaMailSender.send(simpleMailMessage);
    }
}
