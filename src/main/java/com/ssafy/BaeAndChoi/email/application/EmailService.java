package com.ssafy.BaeAndChoi.email.application;

import com.ssafy.BaeAndChoi.exception.BadRequestException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * HTML 이메일 보낼 메서드
     */
    public void sendFindIdHtmlEmail(String toEmail, String userId) {
        try {
            // 1) MimeMessage 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("아이디 찾기 안내");

            // 2) 템플릿 엔진에 변수 세팅
            Context context = new Context();
            context.setVariable("userId", userId);

            // 3) HTML 렌더링
            String html = templateEngine.process("email/find-id", context);

            helper.setText(html, true);  // true = HTML 모드

            // 4) 전송
            mailSender.send(message);
        } catch (Exception e) {
            // 실패 시 예외 처리
            throw new BadRequestException("메일 전송 실패: " + e.getMessage());
        }
    }
}

