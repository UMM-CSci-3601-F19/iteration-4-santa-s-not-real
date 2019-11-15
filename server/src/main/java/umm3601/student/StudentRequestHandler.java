package umm3601.student;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.bson.Document;
import spark.Request;
import spark.Response;
import umm3601.GoogleAuth;
import umm3601.Server;
import umm3601.student.StudentController;

import java.util.Collections;
import java.util.List;

public class StudentRequestHandler {
  private final StudentController studentController;
  private final GoogleAuth gauth;


  private static final String CLIENT_ID = "375549452265-kpv6ds6lpfc0ibasgeqcgq1r6t6t6sth.apps.googlestudentcontent.com";

  private static final String CLIENT_SECRET_FILE = "../secret.json";

  private static NetHttpTransport transport = new NetHttpTransport();

  public StudentRequestHandler(StudentController studentController, GoogleAuth gauth) {
    this.studentController = studentController;
    this.gauth = gauth;
  }

  public String getStudentJSON(Request req, Response res) {
    res.type("application/json");
    String id = req.params("id");
    String student;
    try {
      student = studentController.getStudent(id);
    } catch (IllegalArgumentException e) {
      // This is thrown if the ID doesn't have the appropriate
      // form for a Mongo Object ID.
      // https://docs.mongodb.com/manual/reference/method/ObjectId/
      res.status(400);
      res.body("The requested student ID, " + id + ", wasn't a legal Mongo Object ID.\n" +
        "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
      return "";
    }
    if (student != null) {
      return student;
    } else {
      res.status(404);
      res.body("The requested student with id " + id + " was not found");
      return "";
    }
  }

  public String getStudents(Request req, Response res) {
    res.type("application/json");
    return studentController.getStudents(req.queryMap().toMap());
  }



  public String login(Request req, Response res) {
    res.type("application/json");

    Document body = Document.parse(req.body());
    String token = body.getString("idtoken"); //key formerly 'code'
    GoogleIdToken idToken = gauth.auth(token);
    if (idToken != null) {
      GoogleIdToken.Payload payload = idToken.getPayload();
      String studentId = payload.getSubject();
      String email = payload.getEmail();
      String name = (String) payload.get("name");
      return studentController.login(studentId, email, name);
    } else {
      return null;
    }
  }
  public String signup(Request req, Response res) {
    res.type("application/json");

    Document body = Document.parse(req.body());
    GoogleIdToken idToken = gauth.auth(body);
    if (idToken != null) {
      GoogleIdToken.Payload payload = idToken.getPayload();
      String studentId = payload.getSubject();
      String email = payload.getEmail();
      String name = (String) payload.get("name");
      String pictureUrl = (String) payload.get("picture");
      return studentController.signup(studentId, email, name, pictureUrl);
    }else{
      return null;
    }
  }
}
