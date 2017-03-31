package com.sparkonix.auth;

import com.sparkonix.entity.User;

import io.dropwizard.auth.Authorizer;

public class WebAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
    	if (user.isUserInRole(role))
			return true;

		return false;   
    }
}
