package com.example.EmailVerification;
import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class EmailVerification {

    private final String username;
    private final String password;
    private final Properties props;
    private final Session session;

    public EmailVerification() {
        Dotenv dotenv = Dotenv.load();
        username = dotenv.get("GMAIL_USERNAME");
        password = dotenv.get("GMAIL_PASSWORD");

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("이메일 전송 완료.");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 랜덤 숫자 생성
        return String.valueOf(code);
    }

    public static void main(String[] args) {
        EmailVerification emailVerification = new EmailVerification();
        Scanner scanner = new Scanner(System.in);

        System.out.print("이메일을 입력해주세요 : ");
        String recipient = scanner.nextLine();

        String verificationCode = generateVerificationCode();
        String subject = "Connect Community 인증 번호 발송";
        String body = "인증 번호는 " + verificationCode + " 입니다.";

        emailVerification.sendEmail(recipient, subject, body);

        while(true){
            System.out.print("인증 번호를 입력해주세요 : ");
            String inputCode = scanner.nextLine();

            if (verificationCode.equals(inputCode)) {
                System.out.println("인증 완료!");
                scanner.close();
                break;
            } else {
                System.out.println("인증 실패..");
            }
        }




    }
}
