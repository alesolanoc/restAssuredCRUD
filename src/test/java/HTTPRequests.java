import io.restassured.internal.ValidatableResponseOptionsImpl;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.testng.Assert;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.github.javafaker.Faker;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class HTTPRequests {
    int id;

    @Test(priority = 1)
    void getUsers() {
        given()
                .when().get("https://reqres.in/api/users?page=2")
                .then().statusCode(200)
                .body("page", equalTo(2))
                .log().all();
    }

    @Test(priority = 2)
    void createUser() {
        HashMap data = new HashMap();
        data.put("name", "Alejandro");
        data.put("job", "tester");
        System.out.println(data);
        id = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/users")
                .jsonPath().getInt("id");
        System.out.println(id);
    }

    @Test(priority = 3)
    void updateUser() {
        HashMap data = new HashMap();
        data.put("name", "Alejandros");
        data.put("job", "testers");
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .put("https://reqres.in/api/users/" + id)
                .then()
                .statusCode(200).log().all();
    }

    @Test(priority = 4)
    void deleteUser() {
        given()
                .when().delete("https://reqres.in/api/users/" + id)
                .then().statusCode(204);
    }

    @Test(priority = 5)
    void createUserUsingJSONObject() {
        JSONObject data = new JSONObject();
        data.put("name", "Alejandross");
        data.put("job", "testerss");
        given()
                .contentType("application/json")
                .body(data.toString())
                .when()
                .post("https://reqres.in/api/users")
                .then().statusCode(201)
                .body("name", equalTo("Alejandross"))
                .header("Content-Type", "application/json; charset=utf-8")
                .log().all();
    }

    @Test(priority = 6)
    void createUserUsingPOJO() {
        Pojo_PostRequest data = new Pojo_PostRequest();
        data.setName("Alejandrosssss");
        data.setJob("testersssss");
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/users")
                .then().statusCode(201)
                .body("name", equalTo("Alejandrosssss"))
                .header("Content-Type", "application/json; charset=utf-8")
                .log().all();
    }

    @Test(priority = 7)
    void createUserUsingExternalJSonFile() throws IOException, ParseException {
        Object o = new JSONParser().parse(new FileReader(".\\body.json"));
        JSONObject data = (JSONObject) o;
        String Name = (String) data.get("name");
        String College = (String) data.get("job");
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/users")
                .then().statusCode(201)
                //.body("name",equalTo("Alejandrorrr"))
                .header("Content-Type", "application/json; charset=utf-8")
                .log().all();
    }

    @Test(priority = 8)
    void getAUser() {
        //  https://reqres.in/api/users?page=5&id=5
        given()
                .pathParam("mypath", "users")
                .queryParam("page", 2)
                .queryParam("id", 8)
                .when()
                .get("https://reqres.in/api/{mypath}")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test(priority = 9)
    void getFromXML() {
        given()
                /*         .pathParam("mypath","Traveler")
                         .queryParam("page",1)
                         .contentType("application/xml")*/
                .when()

                .get("http://restapi.adequateshop.com/api/Traveler?page=1")
                .then()
                .statusCode(200)
                .header("Content-Type", "application/xml; charset=utf-8")
                .body("TravelerinformationResponse.travelers.Travelerinformation[1].name", equalTo("AS"));
    }

    @Test(priority = 10)
    void getFromXMLwithAssert() {
        Response res = given()
                .when()
                .get("http://restapi.adequateshop.com/api/Traveler?page=1");
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertEquals(res.header("Content-Type"), "application/xml; charset=utf-8");
        String pageMumber = res.xmlPath().get("TravelerinformationResponse.page").toString();
        Assert.assertEquals(pageMumber, "1");
    }

    @Test(priority = 12)
    void getFromXMLwithAssertToListString() {
        Response res = given()
                .when()
                .get("http://restapi.adequateshop.com/api/Traveler?page=1");
        XmlPath xmlobj = new XmlPath(res.asString());
        // verify total number of travfellers
        List<String> travellers = xmlobj.getList("TravelerinformationResponse.travelers.Travelerinformation");
        Assert.assertEquals(travellers.size(), 10);
        // verify traveller name is present in response
        List<String> traveller_names = xmlobj.getList("TravelerinformationResponse.travelers.Travelerinformation.name");
        Boolean status = false;
        for (String travellerName : traveller_names) {
            System.out.println(travellerName);
            if (travellerName.equals("Developer")) {
                status = true;
                break;
            }
        }
        Assert.assertEquals(status, true);
    }

    @Test(priority = 13)
    void testBasicAuthentication() {
        given()
                .auth().basic("postman", "password")
                .when()
                .get("https://postman-echo.com/basic-auth")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .log().all();
    }

    @Test(priority = 14)
    void testDigestAuthentication() {
        given()
                .auth().digest("postman", "password")
                .when()
                .get("https://postman-echo.com/basic-auth")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .log().all();
    }

    @Test(priority = 14)
    void testPreemptiveAuthentication() {
        given()
                .auth().preemptive().basic("postman", "password")
                .when()
                .get("https://postman-echo.com/basic-auth")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .log().all();
    }

    @Test(priority = 15)
    void testBearedTokenAuthentication() {
        String bearedToken = "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";
        given()
                .headers("Authorization", "Bearer " + bearedToken)
                .when()
                .get("https://api.github.com/user/repos")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test(priority = 16)
    void testOATHAuthentication() {
        String bearedToken = "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";
        given()
                .auth().oauth2(bearedToken)
                .when()
                .get("https://api.github.com/user/repos")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test(priority = 17)
    void test_createUser(ITestContext context) {
        Faker faker = new Faker();
        JSONObject data = new JSONObject();
        data.put("name", faker.name().fullName());
        data.put("gender", "Male");
        data.put("email", faker.internet().emailAddress());
        data.put("status", "inactive");
        String bearedToken = "78100a0726b67f9c6d84b555717a9e212602a4f7150e5f6a60ec6400efc06886";
        id = given()
                .headers("Authorization", "Bearer " + bearedToken)
                .contentType("application/json")
                .body(data.toString())
                .when()
                .post("https://gorest.co.in/public/v2/users")
                .jsonPath().getInt("id");
        System.out.println(id);
        context.setAttribute("user_id", id);
    }

    @Test(priority = 18)
    void test_getUser(ITestContext context) {
        int id = (Integer) context.getAttribute("user_id");
        String bearedToken = "78100a0726b67f9c6d84b555717a9e212602a4f7150e5f6a60ec6400efc06886";
        given()
                .headers("Authorization", "Bearer " + bearedToken)
                .pathParam("id", id)
                .when()
                .get("https://gorest.co.in/public/v2/users/{id}")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test(priority = 19)
    void test_updateUser(ITestContext context) {
        int id = (Integer) context.getAttribute("user_id");
        Faker faker = new Faker();
        JSONObject data = new JSONObject();
        data.put("name", faker.name().fullName());
        data.put("gender", "Male");
        data.put("email", faker.internet().emailAddress());
        data.put("status", "inactive");
        String bearedToken = "78100a0726b67f9c6d84b555717a9e212602a4f7150e5f6a60ec6400efc06886";
        given()
                .headers("Authorization", "Bearer " + bearedToken)
                .contentType("application/json")
                .pathParam("id", id)
                .body(data.toString())
                .when()
                .put("https://gorest.co.in/public/v2/users/{id}")
                .then()
                .log().all();
        System.out.println(id);
    }

    @Test(priority = 20)
    void deletedUser(ITestContext context) {
        int id = (Integer) context.getAttribute("user_id");
        String bearedToken = "78100a0726b67f9c6d84b555717a9e212602a4f7150e5f6a60ec6400efc06886";
        given()
                .headers("Authorization", "Bearer " + bearedToken)
                .pathParam("id", id)
                .when()
                .delete("https://gorest.co.in/public/v2/users/{id}")
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test(priority = 21)
    void createUser1() {
        HashMap data = new HashMap();
        data.put("name", "Alejandro");
        data.put("job", "tester");
        data.put("job1", "tester1");
        data.put("job2", "tester2");
        data.put("job3", "tester3");
        data.put("job4", "tester4");
        System.out.println(data);
        id = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/users")
                .jsonPath().getInt("id");
        System.out.println(id);
    }


    @Test(priority = 22)
    void getMyUsers() {
        given()
                .when().get("http://localhost:3000/users")
                .then().statusCode(200)
                .log().all();
    }

    @Test(priority = 23)
    void getMyUser() {
        String res = given()
                .pathParam("mypath", "users")
                .queryParam("id", 2)
                .when()
                .get("http://localhost:3000/{mypath}")
                .thenReturn().asString();
        JsonPath jsonPath = new JsonPath(res);
        System.out.println(jsonPath);
        System.out.println(res);
        String valor = jsonPath.getString("name");
        System.out.println(valor + "asdfasfd");
        String answer = jsonPath.getString("answers.field1");
        System.out.println(answer + "1111111");
        String question = jsonPath.getString("questions[0][1]");
        System.out.println(question + "222222");
        List<String> answers = jsonPath.getList("answers");
        System.out.println(answers);
        List<String> questions = jsonPath.getList("questions");
        System.out.println(questions);
        Assert.assertEquals("[Sarah Edo2]", jsonPath.getString("name"));
        Assert.assertEquals("question22", jsonPath.getString("questions[0][1]"));
    }

    @Test(priority = 24)
    void postMyUser() {
        Pojo_PostRequest2 person = new Pojo_PostRequest2();
        person.setName("Alejandross6");
        person.setAvatarURL("testerss6");
        Answers answers = new Answers();
        answers.setField1("OptionOne");
        answers.setField2("OptionTwo");
        answers.setField3("OptionThree");
        answers.setField4("OptionFour");
        List<String> questionss = new ArrayList<String>();
        questionss.add("question15");
        questionss.add("question25");
        person.setQuestions(questionss);
        List<Answers> allAnswers = new ArrayList();
        allAnswers.add(answers);
        System.out.println(answers);
        System.out.println(allAnswers);
        person.setAnswers(answers);

        given()
                .contentType("application/json")
                .body(person)
                .when()
                .post("http://localhost:3000/users")
                .then().statusCode(201)
                .log().all();
    }
    @Test(priority = 25)
    void putMyUser() {
        Pojo_PostRequest2 person = new Pojo_PostRequest2();
        person.setName("Alejandross6555");
        person.setAvatarURL("testerss6555");
        Answers answers = new Answers();
        answers.setField1("OptionOne222");
        answers.setField2("OptionTwo222");
        answers.setField3("OptionThree222");
        answers.setField4("OptionFour222");
        List<String> questionss = new ArrayList<String>();
        questionss.add("question15222");
        questionss.add("question25222");
        person.setQuestions(questionss);
        List<Answers> allAnswers = new ArrayList();
        allAnswers.add(answers);
        System.out.println(answers);
        System.out.println(allAnswers);
        person.setAnswers(answers);

        given()
                .pathParam("mypath", "users")
            //    .queryParam("id", 20)
                .contentType("application/json")
                .body(person)
                .when()
                .put("http://localhost:3000/{mypath}/2")
                .then()//.statusCode(201)
                .log().all();
    }

    @Test(priority = 26)
    void deleteMyUser() {
        given()
                .pathParam("mypath", "users")
                .when().delete("http://localhost:3000/{mypath}/4")
                .then().statusCode(200);
    }
}
