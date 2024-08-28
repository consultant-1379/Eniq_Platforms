package com.distocraft.dc5000.etl.mediation.smtp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;

public class SMTPMediationAction extends TransferActionBase {

  private Logger log = Logger.getLogger("mediator.smtp");

  private Logger flog = Logger.getLogger("mediator.smtp.file");

  final private Properties conf;

  public SMTPMediationAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact,
      Meta_transfer_actions trActions, SetContext setcontext) throws EngineMetaDataException {

    try {
      Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
      whereCollSet.setEnabled_flag("Y");
      whereCollSet.setCollection_set_id(collectionSetId);
      Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);

      String techPack = collSet.getCollection_set_name();
      String set_type = collection.getSettype();
      String set_name = collection.getCollection_name();

      log = Logger.getLogger("etl." + techPack + "." + set_type + "." + set_name + ".mediator.smtp");
      flog = Logger.getLogger("file." + techPack + "." + set_type + "." + set_name + ".mediator.smtp");

    } catch (Exception e) {
      log.log(Level.WARNING, "Error initializing loggers", e);
    }

    String act_cont = trActions.getAction_contents();

    conf = new Properties();

    if (act_cont != null && act_cont.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        conf.load(bais);
        bais.close();
        log.finest("Configuration read");
      } catch (Exception e) {
        throw new EngineMetaDataException("Error reading config", new String[] { "" }, e, this, this.getClass()
            .getName());
      }
    }
  }

  public void execute() throws EngineException {
    final String to = conf.getProperty("to");
    final String from = conf.getProperty("from");
    final String host = conf.getProperty("mail.smtp.host");
    final String filepath = conf.getProperty("filepath");
    final String subject = conf.getProperty("subject");

    final boolean sendFilesAsAttachments = new Boolean(conf.getProperty("sendFilesAsAttachments")).booleanValue();
    final Integer numberOfFilesPerEmail = new Integer(conf.getProperty("numberOfFilesPerEmail"));
    Multipart mp = new MimeMultipart();
    int fileCounter = 0;
    String messageText = "";

    if (to == null || from == null || host == null || filepath == null || subject == null) {
      log.severe("Unsufficient configuration. Exiting...");
      return;
    }

    log.info("Getting session instance...");
    final Session session = Session.getInstance(conf);
    session.setDebug(false);
    log.info("Starting to get attachmentfiles list in path " + filepath);

    // Get the list of attachmentfiles.
    final File filedir = new File(filepath);
    log.info("filedir.toString() = " + filedir.toString());

    if (!filedir.isDirectory()) {
      log.severe("Fildir is not a directory! Exiting...");
      return;
    } 

    if (!filedir.canRead()) {
      log.severe("Filedir cannot be read! Exiting...");
      return;
    } 

    if (filedir == null) {
      log.severe("filedir is null! Exiting...");
      return;
    }

    final File[] files = filedir.listFiles();

    log.info("Attachmentpath contains " + files.length + " files.");

    try {
      // Start iterating through the files in the attachmentpath.
      for (int i = 0; i < files.length; i++) {

        if (files[i].isFile() && files[i].canRead()) {
          fileCounter++;

          if (sendFilesAsAttachments) {
            // create the file attachment part
            final MimeBodyPart mbpAttachment = new MimeBodyPart();
            // attach the file to the message
            final FileDataSource fds = new FileDataSource(files[i].getAbsolutePath());
            mbpAttachment.setDataHandler(new DataHandler(fds));
            mbpAttachment.setFileName(fds.getName());

            // create the Multipart and add its parts to it
            mp.addBodyPart(mbpAttachment);
            flog.info("Created file " + files[i].getAbsolutePath() + " as email attachment.");
          } else {
            // Send the files as the message's text.
            try {
              final BufferedReader in = new BufferedReader(new FileReader(files[i].getAbsolutePath()));

              String line = "";
              while ((line = in.readLine()) != null) {
                messageText += line + "\r\n";
              }
              flog.info("Added file " + files[i].getAbsolutePath() + " contents to email's text content.");
              in.close();
            } catch (Exception e) {
              flog.severe("File input error: " + e.getMessage() + "");
              if (e instanceof SendFailedException) {
                flog.severe("File " + files[i].getAbsolutePath() + " read error.");
              }
            }
          }
        }
        else {
          log.info("File " + files[i] + " is not a file or can't be read.");
        }
         

        // Check if the email is full of attachments and send it away if it is.
        // Also send the email if the email is incomplete but the loop is about
        // to
        // end.
        if (fileCounter >= numberOfFilesPerEmail.intValue() || numberOfFilesPerEmail.intValue() == 0
            || numberOfFilesPerEmail == null || i == (files.length - 1)) {
          // Time to send the email.
          try {

            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            final InternetAddress[] address = { new InternetAddress(to) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setText(messageText);
            msg.setSentDate(new Date());

            if (sendFilesAsAttachments) {
              // add the Multipart to the message
              msg.setContent(mp);
            }

            log.info("Trying to send the email.");
            // send the message
            Transport.send(msg);
            log.info("Email sent succesfully.");

            // Delete the files sent with the email
            while (fileCounter > 0) {
              final File targetFile = files[i - (fileCounter - 1)].getAbsoluteFile();
              try {
                targetFile.delete();
              } catch (SecurityException e) {
                log.log(Level.WARNING, "Deleting the attachment file failed", e);
              }
              flog.info("Deleted file " + targetFile.getAbsolutePath() + "");
              fileCounter--;
            }

            // Empty the loop variables.
            messageText = "";
            fileCounter = 0;
            mp = new MimeMultipart();

          } catch (Exception e) {
            if (e instanceof SendFailedException) {
              MessagingException sfe = (MessagingException) e;
              if (sfe instanceof SMTPSendFailedException) {
                final SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;
                log.severe("SMTP SEND FAILED:");
                log.severe(ssfe.toString());
                log.severe("Command: " + ssfe.getCommand());
                log.severe("RetCode: " + ssfe.getReturnCode());
                log.severe("Response: " + ssfe.getMessage());
              } else {
                log.severe("Send failed: " + sfe.toString());
              }
              Exception ne;
              while ((ne = sfe.getNextException()) != null && ne instanceof MessagingException) {
                sfe = (MessagingException) ne;
                if (sfe instanceof SMTPAddressFailedException) {
                  final SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;
                  log.severe("ADDRESS FAILED:");
                  log.severe(ssfe.toString());
                  log.severe("Address: " + ssfe.getAddress());
                  log.severe("Command: " + ssfe.getCommand());
                  log.severe("RetCode: " + ssfe.getReturnCode());
                  log.severe("Response: " + ssfe.getMessage());
                } else if (sfe instanceof SMTPAddressSucceededException) {
                  log.severe("ADDRESS SUCCEEDED:");
                  final SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;
                  log.severe(ssfe.toString());
                  log.severe("Address: " + ssfe.getAddress());
                  log.severe("Command: " + ssfe.getCommand());
                  log.severe("RetCode: " + ssfe.getReturnCode());
                  log.severe("Response: " + ssfe.getMessage());
                }
              }
            } else {
              log.severe("Got Exception: " + e);
              e.printStackTrace();
            }
          }
        }

      }
    } catch (MessagingException mex) {
      log.log(Level.WARNING, "Mediation failed exceptionally", mex);
      throw new EngineException("Exception in Mediation", new String[] { "" }, mex, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_SYSTEM);

    }
  }
}
