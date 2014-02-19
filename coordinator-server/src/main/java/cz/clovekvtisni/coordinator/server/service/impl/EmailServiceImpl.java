package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.server.service.EmailService;
import cz.clovekvtisni.coordinator.server.util.SecretInfoServerSide;
import cz.clovekvtisni.coordinator.server.util.TemplateTool;
import cz.clovekvtisni.coordinator.server.util.SignatureTool;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/17/10
 * Time: 2:02 PM
 */
@Service("emailService")
public class EmailServiceImpl extends AbstractServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("body", body);
        sendEmail(to, subject, "generic_body", context);
    }


    @Override
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> contextParams) {
        logger.info("Sending email: " + to + " " + subject);

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SecretInfoServerSide.EMAIL_FROM));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");

            Multipart mp = new MimeMultipart();

            Map<String, Object> context = buildTemplateContext(to);
            context.putAll(contextParams);
            String emailBody;
            try {
                emailBody = TemplateTool.getInstance().processTemplate(templateName, context, Locale.getDefault());
            } catch (Exception e) {
                throw new IllegalStateException("can't parse template", e);
            }

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailBody, "text/html; charset=UTF-8");
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);

            logger.info("sending mail '" + subject + "' to " + to);

            Transport.send(msg);

        } catch (Exception e) {
            throw new IllegalStateException("can't send email to " + to, e);
        }
    }

    protected Map<String, Object> buildTemplateContext(String emailTo) throws UnsupportedEncodingException {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("fromEmail", SecretInfoServerSide.EMAIL_FROM);
        context.put("urlRoot", System.getProperty("cz.clovekvtisni.coordinator.urlRoot"));
        StringBuffer sb = new StringBuffer();
        sb.append(System.getProperty("cz.clovekvtisni.coordinator.urlRoot"));
        sb.append("tools/unsubscribe");
        sb.append("?email="+ URLEncoder.encode(emailTo, "UTF-8"));
        sb.append("&signature=" + URLEncoder.encode(buildUnsubscribeSignature(emailTo), "UTF-8"));

        // set unsubsribe Url to email
        context.put("unsubscribeUrl", sb.toString());
        context.put("showButtons", false);
        return context;
    }

    public String buildUnsubscribeSignature(String emailTo) {
        return SignatureTool.sign(emailTo);
    }

}
