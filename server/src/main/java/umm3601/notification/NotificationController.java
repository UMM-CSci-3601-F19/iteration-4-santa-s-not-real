package umm3601.notification;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import umm3601.student.StudentController;
import umm3601.student.StudentRequestHandler;


public class NotificationController {
  private final MongoCollection<Document> studentCollection;

  public NotificationController(MongoDatabase database) {
    studentCollection = database.getCollection("Students");

  }

}
