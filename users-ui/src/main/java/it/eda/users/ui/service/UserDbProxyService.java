package it.eda.users.ui.service;

import it.eda.users.ui.service.client.UserDbProxyClient;
import it.eda.users.ui.model.response.RestResponse;
import it.eda.users.ui.model.user.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
public class UserDbProxyService {

    @Inject
    @RestClient
    UserDbProxyClient userDbProxyClient;


    public Response login(User user) {
        return userDbProxyClient.login(user);
    }
    public Response register(User user){
        return userDbProxyClient.register(user);
    }





}
