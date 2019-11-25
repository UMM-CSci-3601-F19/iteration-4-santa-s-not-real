package umm3601.notification;

import com.google.gson.JsonParser;
import org.bson.Document;
import spark.Request;
import spark.Response;

public class NotificationRequestHandler {

  private final GmailQuickstart notificationController;

  public NotificationRequestHandler(GmailQuickstart notificationController) {this.notificationController = notificationController;}

  public String subscribe(Request req, Response res) {
    res.type("application/json");
    Document newSubscription = Document.parse(req.body());
    String email = newSubscription.getString("email");
    String type = newSubscription.getString("type");
    String id = newSubscription.getString("id");

    System.out.println("[subscribe] INFO notification.NotificationRequestHandler - Adding new subscription [email=" + email + ", type=" + type + ", id=" + id + ']');
    return notificationController.addNewSubscription(email, type, id);
  }
}
