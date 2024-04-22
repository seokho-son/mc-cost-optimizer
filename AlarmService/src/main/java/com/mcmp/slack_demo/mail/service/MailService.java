package com.mcmp.slack_demo.mail.service;

import com.mcmp.slack_demo.mail.model.MailMessage;

import jakarta.activation.FileDataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;


@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String sendEmail(MailMessage mailMessage, String type, ClassPathResource file){
        MimeMessage mimeMessage = emailSender.createMimeMessage();

        if (type.equals("password")){
            // 메일의 타입에 따른 설정
        }

        try {
            Context context = new Context();
            context.setVariable("username", "테스트유저");
            String html = templateEngine.process("WelcomeT.html", context);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(setToList(mailMessage.getTo()));
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mimeMessageHelper.setText(html, true);

            ClassPathResource resource = new ClassPathResource("static/images/mcmp-logo.png");
            mimeMessageHelper.addInline("logo_png", resource.getFile());

            if("invoice".equals(type)){
                mimeMessageHelper.addAttachment(MimeUtility.encodeText("test.txt", "UTF-8", "B"), file.getFile());
            }

            emailSender.send(mimeMessage);

            log.info("############Send AlertEmail Success############");
            return "Success";

        } catch (Exception e) {
            log.info("############Send AlertEmail Fail############");
            throw new RuntimeException(e);
        }
    }

    private String[] setToList(List<String> toList){
        return toList.toArray(new String[toList.size()]);
    }

}
