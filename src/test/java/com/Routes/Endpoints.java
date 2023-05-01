package com.Routes;

import com.pojos.AccessToken;
import com.pojos.Account;
import com.pojos.Accounts;
import com.utilities.Readprop;

public class Endpoints {
    public static String accountApi= Readprop.getData("account_api");
    public static String loginAPI=Readprop.getData("Login");
    public static String adminAPI=Readprop.getData("admin_api");




}
