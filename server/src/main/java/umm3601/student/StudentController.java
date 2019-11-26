package umm3601.student;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
//Todo: Maybe find something not deprecated.
import com.mongodb.util.JSON;
import umm3601.GoogleAuth;

import static com.mongodb.client.model.Filters.eq;

public class StudentController {

  private final MongoCollection<Document> studentCollection;

  public StudentController(MongoDatabase database) {
    studentCollection = database.getCollection("Students");
  }

  public String getStudent(String id) {
    FindIterable<Document> jsonUsers
      = studentCollection
      .find(eq("_id", new ObjectId(id)));

    Iterator<Document> iterator = jsonUsers.iterator();
    if (iterator.hasNext()) {
      Document user = iterator.next();
      return user.toJson();
    } else {
      // We didn't find the desired user
      return null;
    }
  }

  public String getStudents(Map<String, String[]> queryParams) {

    Document filterDoc = new Document();

    if (queryParams.containsKey("age")) {
      int targetAge = Integer.parseInt(queryParams.get("age")[0]);
      filterDoc = filterDoc.append("age", targetAge);
    }

    if (queryParams.containsKey("company")) {
      String targetContent = (queryParams.get("company")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("company", contentRegQuery);
    }

    //FindIterable comes from mongo, Document comes from Gson
    FindIterable<Document> matchingStudent = studentCollection.find(filterDoc);

    return serializeIterable(matchingStudent);
  }

  private String serializeIterable(Iterable<Document> documents) {

    return StreamSupport.stream(documents.spliterator(), false)
      .map((Document d) -> d.toJson())
      .collect(Collectors.joining(", ", "[", "]"));
  }

  String signup(String studentId, String email, String name, String pictureUrl){
    Document filterDoc = new Document();

    Document contentRegQuery = new Document();
    contentRegQuery.append("$regex", studentId);
    contentRegQuery.append("$options", "i");
    filterDoc = filterDoc.append("studentId", contentRegQuery);

    FindIterable<Document> matchingStudents = studentCollection.find(filterDoc);

    if(JSON.serialize(matchingStudents).equals("[ ]")) {
      ObjectId id = new ObjectId();

      Document newStudent = new Document();
      newStudent.append("_id", id);
      newStudent.append("studentId", studentId);
      newStudent.append("name", name);
      newStudent.append("bio", "Nothing here yet");
      newStudent.append("email", email);
      newStudent.append("totalReviewScore", 0);
      newStudent.append("numReviews", 0);

      newStudent.append("pictureUrl", pictureUrl);
      try {
        studentCollection.insertOne(newStudent);
        // return JSON.serialize(newStudent);
        Document studentInfo = new Document();
        studentInfo.append("_id", matchingStudents.first().get("_id"));
        studentInfo.append("email", matchingStudents.first().get("email"));
        studentInfo.append("name", matchingStudents.first().get("name"));
        studentInfo.append("pictureUrl", matchingStudents.first().get("pictureUrl"));
        System.err.println("Successfully added new student [_id=" + id + ", studentId=" + studentId + " email=" + email + " name=" + name + " pictureUrl " + pictureUrl + "]");
        return "New Student added";
      }catch(MongoException e){
        e.printStackTrace();
        return "Error trying to create student";
      }
    }else {
      return "Student already exists";
    }
  }

  String login(String studentId, String email, String name) {

    System.out.println("Checking database for student");
    FindIterable<Document> matchingStudent = studentCollection.find(eq("studentId", studentId));

    System.out.println("Is this a new student?   " + serializeIterable(matchingStudent).equals("[]"));

    if (serializeIterable(matchingStudent).equals("[]")) {
      ObjectId id = new ObjectId();
      Document newStudent = new Document();

      newStudent.append("_id", id);
      newStudent.append("studentId", studentId);
      newStudent.append("name", name);
      newStudent.append("bio", "Nothing here yet");
      newStudent.append("email", email);
      newStudent.append("totalReviewScore", 0);
      newStudent.append("numReviews", 0);
      try {
        studentCollection.insertOne(newStudent);
        System.out.println("Successfully added new student [_id: " + id + " | studentId: " + studentId + " | name: " + name + " | bio: Nothing here yet " + " | email: " + email + " | totalReviewScore: 0 | numReviews: 0");
        return "New student successfully added";
      } catch (MongoException e) {
        e.printStackTrace();
        return "Error trying to create student";
      }
    } else {
      System.out.println(serializeIterable(matchingStudent));

      Document filter = new Document("studentId", studentId);
      Document getName = new Document();


      getName.append("name", name);


      Document setName = new Document("$set", getName);

      try {
        studentCollection.updateOne(filter, setName);
        System.out.println("Updating a student [studentId: " + studentId + " | name: " + name + " was successful");
        return "Success in logging in returning student";
      } catch (MongoException e) {
        e.printStackTrace();
        return "Error trying to log in a returning student";
      }
    }
  }
  String getEmailAddress(String email){
    System.out.println("Checking database for student");
    FindIterable<Document> matchingStudent = studentCollection.find(eq("email", email));
    if (serializeIterable(matchingStudent).equals("[]")){
      return null;
    }
    else {
      return email;
    }

  }
}
