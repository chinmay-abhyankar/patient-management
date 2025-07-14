import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientIntegrationTest {
  @BeforeAll
  static void setUp(){
    RestAssured.baseURI = "http://localhost:4004";
  }

  @Test
  public void shouldReturnPatientsWithValidToken () {
    String token = getToken();

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/api/patients")
        .then()
        .statusCode(200)
        .body("patients", notNullValue());
  }


  // This test method verifies the rate limiting functionality of the API
  // It sends multiple requests in quick succession to trigger the rate limit
  @Test
  public void shouldReturn429AfterLimitExceeded() throws InterruptedException {
    // Get authentication token
    String token = getToken();
    // Set total number of requests to make
    int total = 20;
    // Counter for number of rate limited responses received
    int tooManyRequest = 0;

    // Loop to send multiple requests
    for(int i=1;i<= total;i++){
      // Send GET request to patients endpoint with auth token
      Response response = given()
              .header("Authorization", "Bearer " + token)
              .when()
              .get("/api/patients");

      // Log request number and response status
      System.out.printf("Request %d -> Status : %d%n",i,response.statusCode());

      // Increment counter if rate limit response (429) received
      if (response.statusCode() == 429) {
        tooManyRequest++;
      }

      // Add delay between requests
      //Thread.sleep(100);
    }

    // Verify that at least one request was rate limited
    assertTrue(tooManyRequest >= 1,"Expected 1 request to be rate limited ");

  }


  private static String getToken() {
    String loginPayload = """
          {
            "email": "testuser@test.com",
            "password": "password123"
          }
        """;

    String token = given()
        .contentType("application/json")
        .body(loginPayload)
        .when()
        .post("/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()
        .get("token");
    return token;
  }
}
