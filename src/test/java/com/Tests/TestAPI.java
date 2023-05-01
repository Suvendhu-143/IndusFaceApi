/*
Author:Suvendhu Shekhar Pradhan
Assignment: Indus Face API test
 */
package com.Tests;

import com.Routes.Endpoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payloadmethods.payloads;
import com.pojos.*;
import com.utilities.Readprop;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestAPI {
    Readprop readprop;
    Login login;
    ObjectMapper objectMapper;
    AccessToken token;
    TransDate transdate;
    NewUser usr;
    Accounts as;
    ChangePassword chPas;

    @BeforeTest
    public void setup_data() {
        readprop = new Readprop();
        token = new AccessToken();
        transdate= new TransDate();
        usr = new NewUser();
        chPas=new ChangePassword();
        objectMapper = new ObjectMapper();
        Accounts as = new Accounts();
        login = new Login();
    }
   //Below method tests login functionality
    @Test(priority = 0)
    public void login_Test() throws IOException {
        login.setUsername(readprop.getData("username"));
        login.setPassword(readprop.getData("password"));
        Response rs = payloads.post(login, Endpoints.loginAPI);
        String respon = rs.getBody().asString();
        token = objectMapper.readValue(respon, AccessToken.class);
        Assert.assertEquals(rs.getStatusCode(), 200);
        Assert.assertEquals(token.getSuccess(), "jsmith is now logged in");
        Assert.assertEquals(rs.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs.getHeader("server"),"Apache-Coyote/1.1");

    }
    //Below method validates accesstoken
    @Test(priority = 1)
    public void testAccess() throws IOException {
        Response rs = payloads.get(Endpoints.loginAPI, token.getAuthorization());
        JsonPath json = new JsonPath(rs.asString());
        String message = json.getString("loggedin");
        System.out.println(message);
        Assert.assertEquals(rs.getStatusCode(), 200);
        Assert.assertEquals(message, "true");
        Assert.assertEquals(rs.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs.getHeader("server"),"Apache-Coyote/1.1");
    }
    //Below method verifies the type of accounts
    @Test(priority = 2)
    public void test_accounts() throws IOException {
        Response rs2 = payloads.get(Endpoints.accountApi,token.getAuthorization());
        as = objectMapper.readValue(rs2.getBody().asString(), Accounts.class);
        System.out.println(as);
        Assert.assertEquals(rs2.getStatusCode(), 200);
        List<Account> as1 = as.getAccounts();
        System.out.println(as1.get(0).getName());
        Assert.assertEquals(as1.get(0).getName(), "Savings");
        Assert.assertEquals(as1.get(1).getName(), "Checking");
        Assert.assertEquals(as1.get(2).getName(), "Credit Card");
        Assert.assertEquals(as1.size(),3);
        Assert.assertEquals(rs2.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs2.getHeader("server"),"Apache-Coyote/1.1");

    }
   //Below method verifies account details
    @Test(priority = 3)
    public void test_account_details() throws IOException
    {   String accountId=as.getAccounts().get(0).getId();
        String accountApiId= Endpoints.accountApi+"/"+accountId;
        Response rs3=payloads.get(accountApiId,token.getAuthorization());
        Assert.assertEquals(rs3.getStatusCode(), 200);
        JsonPath json = new JsonPath(rs3.asString());
        Assert.assertEquals(json.getString("accountId"),accountId);
        Assert.assertEquals(json.getString("credits[0].date"),"2004-12-29");
        Assert.assertEquals(json.getString("credits[0].amount"),"1200");
        Assert.assertEquals(json.getString("credits[0].description"),"Paycheck");
        Assert.assertEquals(json.getString("credits[0].account"),"1001160140");
        Assert.assertEquals(json.getString("debits[0].date"),"2005-01-17");
        Assert.assertEquals(json.getString("debits[0].amount"),"2.85");
        Assert.assertEquals(json.getString("debits[0].description"),"Withdrawal");
        Assert.assertEquals(json.getString("debits[0].account"),"1001160140");
        System.out.println(rs3.asString());
        Assert.assertEquals(rs3.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs3.getHeader("server"),"Apache-Coyote/1.1");
    }

    //Below method verifies last transaction details
    @Test(priority=4)
    public void test_last_trans() throws IOException
    {   Date dateNow = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        //use 'date_current' for getting current date in string format
        String date_current = date_format.format(dateNow);
        String accountApiTrans= Endpoints.accountApi+"/"+as.getAccounts().get(0).getId()+"/transactions";
        Response rs3=payloads.get(accountApiTrans,token.getAuthorization());
        Assert.assertEquals(rs3.getStatusCode(), 200);
        JsonPath json = new JsonPath(rs3.asString());
        System.out.println(rs3.asString());
        //current date of api is yet to be get refreshed from server
        Assert.assertEquals(json.getString("last_10_transactions[0].date"),"2023-05-01");
        Assert.assertEquals(json.getString("last_10_transactions.size()"),"10");
        Assert.assertEquals(rs3.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs3.getHeader("server"),"Apache-Coyote/1.1");

    }
    //Below method verifies last transactions within specified dates
    @Test(priority=5)
    public void test_trans_with_dates() throws IOException
    {
        String accountApiTrans= Endpoints.accountApi+"/"+as.getAccounts().get(0).getId()+"/transactions";
        transdate.setStartDate(readprop.getData("startdate"));
        transdate.setEndDate(readprop.getData("enddate"));
        Response rs5=payloads.post(transdate,accountApiTrans, token.getAuthorization());
        Assert.assertEquals(rs5.getStatusCode(), 200);
        JsonPath json = new JsonPath(rs5.asString());
        System.out.println(rs5.asString());
        Assert.assertEquals(json.getString("transactions[0]"),null);
        Assert.assertEquals(rs5.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs5.getHeader("server"),"Apache-Coyote/1.1");

    }
    //Below method verifies the adduser functionality of admin user
    @Test(priority=6)
    public void add_user() throws IOException
    {
        String addUserApi=Endpoints.adminAPI+"/addUser";
        usr.setFirstname(readprop.getData("firstname"));
        usr.setLastname(readprop.getData("lastname"));
        usr.setUsername(readprop.getData("username1"));
        usr.setPassword1(readprop.getData("password1"));
        usr.setPassword2(readprop.getData("password2"));
        Response rs6=payloads.post(usr,addUserApi,token.getAuthorization());
        Assert.assertEquals(rs6.getStatusCode(), 200);
        JsonPath json = new JsonPath(rs6.asString());
        System.out.println(rs6.asString());
        Assert.assertEquals(json.getString("success"),"Requested operation has completed successfully.");
        Assert.assertEquals(rs6.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs6.getHeader("server"),"Apache-Coyote/1.1");

    }

    //Below method verifies the Changing of User passwords by admin user
    @Test(priority=7)
    public void change_user_password() throws IOException
    {
        String change_Usr_PassApi=Endpoints.adminAPI+"/changePassword";
        chPas.setUsername(readprop.getData("username2"));
        chPas.setPassword1(readprop.getData("password_1"));
        chPas.setPassword2(readprop.getData("password_2"));
        Response rs7=payloads.post(usr,change_Usr_PassApi,token.getAuthorization());
        Assert.assertEquals(rs7.getStatusCode(), 200);
        JsonPath json = new JsonPath(rs7.asString());
        System.out.println(rs7.asString());
        Assert.assertEquals(json.getString("success"),"Requested operation has completed successfully.");
        Assert.assertEquals(rs7.getHeader("content-type"),"application/json");
        Assert.assertEquals(rs7.getHeader("server"),"Apache-Coyote/1.1");
    }





}
