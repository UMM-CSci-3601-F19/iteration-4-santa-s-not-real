package umm3601.notification;

import umm3601.GoogleAuth;
import umm3601.student.*;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.bson.Document;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

public class GmailQuickstart {

  private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static StudentRequestHandler studentRequestHandler;
  private static GoogleAuth gauth;

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  //private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
  private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);
  private static final String CREDENTIALS_FILE_PATH = "./../../../resources/credentials.json";

  public final MongoCollection<Document> subscriptionCollection;
  public final MongoCollection<Document> machineCollection;

  /**
   * Creates an authorized Credential object.
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */


  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    // Load client secrets.
    InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
      .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
      .setAccessType("offline")
      .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
  public GmailQuickstart(StudentRequestHandler studentRequestHandler, GoogleAuth gauth, MongoDatabase subscriptionDatabase, MongoDatabase machineDatabase){
    this.studentRequestHandler = studentRequestHandler;
    this.gauth = gauth;
    this.subscriptionCollection = subscriptionDatabase.getCollection("subscriptions");
    this.machineCollection = machineDatabase.getCollection("machines");
  }
  //Gmail Setup
  //create email content
  public static MimeMessage createEmail(String to,
                                        String from,
                                        String subject,
                                        String bodyText)
    throws MessagingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(from));
    email.addRecipient(javax.mail.Message.RecipientType.TO,
      new InternetAddress(to));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }

  public static Message createMessageWithEmail(MimeMessage emailContent)
    throws MessagingException, IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }



  //Send message
  public static Message sendMessage(Gmail service,
                                    String userId,
                                    MimeMessage emailContent)
    throws MessagingException, IOException {
    Message message = createMessageWithEmail(emailContent);
    message = service.users().messages().send(userId, message).execute();

    System.out.println("Message id: " + message.getId());
    System.out.println(message.toPrettyString());
    return message;
  }

  //Controller setup
  public void checkMachines() throws IOException, MessagingException, GeneralSecurityException{
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
      .setApplicationName(APPLICATION_NAME)
      .build();

    // Print the labels in the user's account.
    String user = "me";
    ListLabelsResponse listResponse = service.users().labels().list(user).execute();
    List<Label> labels = listResponse.getLabels();
    if (labels.isEmpty()) {
      System.out.println("No labels found.");
    } else {
      System.out.println("Labels:");
      for (Label label : labels) {
        System.out.printf("- %s\n", label.getName());
      }
    }
    Document check = new Document();
    check.append("type", "machine");
    FindIterable<Document> subscriptionsForMachines = subscriptionCollection.find().filter(check);
    for (Document s : subscriptionsForMachines) {
      check = new Document();
      check.append("id", s.get("id"));
    }

    Document vacantMachines = machineCollection.find(check).first();
    if(vacantMachines != null && !vacantMachines.getBoolean("running") && vacantMachines.getString("status").equals("normal")){
      String machineName = transformId(vacantMachines.getString("name"));
      String roomName = transformId(vacantMachines.getString("room_id"));
      String type = vacantMachines.getString("type");
      subscriptionCollection.deleteOne(check);

      sendMessage(service,"rowla070@morris.umn.edu",createEmail(check.toString(),"trow016@gmail.com", "Laundry Notification", ("There is a"+ " "+ type+" open in" + roomName+ "!"  )));
    }
  }

  private String transformId(String str) {
    String transformed = "";
    for (int i = 0; i < str.length(); ++i) {
      if (str.charAt(i) != '-' && str.charAt(i) != '_') {
        transformed += str.charAt(i);
      } else {
        transformed += ' ';
      }
    }
    return transformed;
  }
  public String addNewSubscription(String email, String type, String id) {
    Document newSubscription = new Document();
    newSubscription.append("email", email);
    newSubscription.append("type", type);
    newSubscription.append("id", id);
    try {
      subscriptionCollection.insertOne(newSubscription);
      ObjectId _id = newSubscription.getObjectId("_id");
      System.err.println("[subscribe] INFO mailing.MailingController - Successfully added new subscription [email=" + email + ", type=" + type + ", id=" + id + ']');
      return _id.toHexString();
    } catch (MongoException e) {
      e.printStackTrace();
      return null;
    }
  }

}
