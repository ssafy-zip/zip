package com.ssafy.BaeAndChoi.email.presentation;

import com.ssafy.BaeAndChoi.email.application.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;


}
