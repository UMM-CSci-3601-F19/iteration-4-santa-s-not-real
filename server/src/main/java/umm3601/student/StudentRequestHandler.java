package umm3601.student;

import com.google.api.client.http.javanet.NetHttpTransport;
import spark.Request;
import spark.Response;

public class StudentRequestHandler {
  private final StudentController studentController;


  private static final String CLIENT_ID = "375549452265-kpv6ds6lpfc0ibasgeqcgq1r6t6t6sth.apps.googlestudentcontent.com";

  private static final String CLIENT_SECRET_FILE = "../secret.json";

  private static NetHttpTransport transport = new NetHttpTransport();

  public StudentRequestHandler(StudentController studentController) {
    this.studentController = studentController;
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

}
