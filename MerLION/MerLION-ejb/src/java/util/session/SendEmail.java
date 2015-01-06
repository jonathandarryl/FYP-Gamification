/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.session;

import java.util.Date;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author chongyangsun
 */
public class SendEmail {
    public SendEmail() {}    
    
    public static boolean run(String addressedTo, String subject, String content) {

        //String toAddress = "winexpress.noreply@gmail.com"; // set the email address to send to
        String recipients = addressedTo.trim(); // trim the recipients address to avoid space
        //String contentType = "text/plain"; // set content type -- only plain text
        String fromAddress = "merlion.no.reply@gmail.com";
        String smtpHost = "smtp.gmail.com"; // set the mailbox to send from 
        int smtpPort = 587; // set port
        String username = "merlion.no.reply@gmail.com"; // mailbox address
        String password = "IS3102JiaYou"; // *password*
        
        try
        {
            //Address recipient = new InternetAddress(addressedTo.trim());
            //Address[] recipients = {recipient};
            Properties props = System.getProperties(); //
            props.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getDefaultInstance(props);

            MimeMessage message = new MimeMessage(session); // set message type

            //message.setFrom(new InternetAddress(toAddress));
            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setFrom(new InternetAddress(fromAddress));
            //message.setRecipients(Message.RecipientType.TO, toAddress);
            //message.setReplyTo(InternetAddress.parse(recipients, false));
            message.setSubject(subject);
            
            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = content;
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            message.setSentDate(new Date());

            Transport transport = session.getTransport("smtp"); // set protocol
            transport.connect(smtpHost, smtpPort, username, password); // connect mail server
            transport.sendMessage(message, message.getAllRecipients()); // send email
            transport.close(); // close connection

            return true;
        } catch (MessagingException messagingException) {
            System.out.print(messagingException);
            return false;

        } catch (Exception e) {
            System.out.print(e);
            return false;
        }
    }//run
}
