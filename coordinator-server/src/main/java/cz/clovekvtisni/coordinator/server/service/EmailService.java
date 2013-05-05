package cz.clovekvtisni.coordinator.server.service;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/17/10
 * Time: 2:22 PM
 */
public interface EmailService extends Service {

	void sendEmail(String to, String subject, String body);

}
