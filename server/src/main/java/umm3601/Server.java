package umm3601;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;
import umm3601.student.StudentController;
import umm3601.student.StudentRequestHandler;
import umm3601.history.HistoryController;
import umm3601.history.HistoryRequestHandler;
import umm3601.laundry.LaundryController;
import umm3601.laundry.LaundryRequestHandler;
import umm3601.user.UserController;
import umm3601.user.UserRequestHandler;
import umm3601.notification.GmailQuickstart;
import umm3601.notification.NotificationRequestHandler;
import javax.mail.MessagingException;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
  private static final String userDatabaseName = "dev";
  private static final String machineDatabaseName = "dev";
  private static final String machinePollingDatabaseName = "dev";
  private static final String roomDatabaseName = "dev";
  private static final String roomPollingDatabaseName = "dev";
  private static final String roomHistoryDatabaseName = "dev";
  private static final String studentDatabaseName = "dev";
  private static final int serverPort = 4567;

  private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  public static void main(String[] args) {

    MongoClient mongoClient = new MongoClient();

    MongoDatabase userDatabase = mongoClient.getDatabase(userDatabaseName);
    MongoDatabase machineDatabase = mongoClient.getDatabase(machineDatabaseName);
    MongoDatabase machinePollingDatabase = mongoClient.getDatabase(machinePollingDatabaseName);
    MongoDatabase roomDatabase = mongoClient.getDatabase(roomDatabaseName);
    MongoDatabase roomPollingDatabase = mongoClient.getDatabase(roomPollingDatabaseName);
    MongoDatabase roomsHistoryDatabase = mongoClient.getDatabase(roomHistoryDatabaseName);
    MongoDatabase studentDatabase = mongoClient.getDatabase(studentDatabaseName);

    GoogleAuth gauth = new GoogleAuth(userDatabase);

    UserController userController = new UserController(userDatabase);
    UserRequestHandler userRequestHandler = new UserRequestHandler(userController);
    LaundryController laundryController = new LaundryController(machineDatabase, roomDatabase, machinePollingDatabase, roomPollingDatabase);
    LaundryRequestHandler laundryRequestHandler = new LaundryRequestHandler(laundryController);
    HistoryController historyController = new HistoryController(roomDatabase, machineDatabase, roomsHistoryDatabase);
    HistoryRequestHandler historyRequestHandler = new HistoryRequestHandler(historyController);
    StudentController studentController = new StudentController(studentDatabase);
    StudentRequestHandler studentRequestHandler = new StudentRequestHandler(studentController, gauth);


    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(() -> new PollingService(mongoClient), 0, 1, TimeUnit.MINUTES);

    executorService.scheduleAtFixedRate(laundryController::updateRooms,        0,10, TimeUnit.MINUTES);

    executorService.scheduleAtFixedRate(laundryController::updateMachines,     0, 1, TimeUnit.MINUTES);

    executorService.scheduleAtFixedRate(historyController::updateHistory,      0,30, TimeUnit.MINUTES);


    executorService.scheduleAtFixedRate(() -> {
        try {
          GmailQuickstart.checkMachines();
        } catch (IOException e) {
          System.out.println(e.toString());
        }
      },  0, 5, TimeUnit.MINUTES);

    //Configure Spark
    port(serverPort);

    // Specify where assets like images will be "stored"
    staticFiles.location("/public");

    options("/*", (request, response) -> {

      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

    // Redirects for the "home" page
    redirect.get("", "/");

    Route clientRoute = (req, res) -> {
      InputStream stream = Server.class.getResourceAsStream("/public/index.html");
      return IOUtils.toString(stream);
    };

    get("/", clientRoute);

    /// Endpoints ///////////////////////////////
    /////////////////////////////////////////////

    // List machines

    get("api/rooms", laundryRequestHandler::getRooms);
    get("api/rooms/:room/machines", laundryRequestHandler::getRoomMachines);
    get("api/machines", laundryRequestHandler::getMachines);
    get("api/machines/:machine", laundryRequestHandler::getMachineJSON);
//    get("api/change_machine_status/:machine_id/:status", laundryRequestHandler::changeMachineStatus);

    // Returns the history of the up-coming 24h of last week with the given room id
    get("api/history/:room", historyRequestHandler::getHistory);
    get("api/all_history", historyRequestHandler::getAllHistory);

    // List users, filtered using query parameters

    get("api/users", userRequestHandler::getUsers);
    get("api/users/:id", userRequestHandler::getUserJSON);
    post("api/users/new", userRequestHandler::addNewUser);

    get("api/student", studentRequestHandler::getStudents);
    get("api/student/:id", studentRequestHandler::getStudentJSON);

    get("api/student/:email", studentRequestHandler ::getEmailAddress);

    // An example of throwing an unhandled exception so you can see how the
    // Java Spark debugger displays errors like this.
    get("api/error", (req, res) -> {
      throw new RuntimeException("A demonstration error");
    });
    post("api/login", studentRequestHandler::login);

    // Called after each request to insert the GZIP header into the response.
    // This causes the response to be compressed _if_ the client specified
    // in their request that they can accept compressed responses.
    // There's a similar "before" method that can be used to modify requests
    // before they they're processed by things like `get`.
    after("*", Server::addGzipHeader);

    get("/*", clientRoute);

    // Handle "404" file not found requests:
    notFound((req, res) -> {
      res.type("text");
      res.status(404);
      return "Sorry, we couldn't find that!";
    });
  }

  // Enable GZIP for all responses
  private static void addGzipHeader(Request request, Response response) {
    response.header("Content-Encoding", "gzip");
  }
}
