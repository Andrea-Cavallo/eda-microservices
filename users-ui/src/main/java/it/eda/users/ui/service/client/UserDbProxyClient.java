package it.eda.users.ui.service.client;


import it.eda.users.ui.model.response.RestResponse;
import it.eda.users.ui.model.user.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


import it.eda.users.ui.model.response.RestResponse;
import it.eda.users.ui.model.user.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/users")
@RegisterRestClient(configKey = "user-db-proxy")
public interface UserDbProxyClient {



    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response login(User user);


    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response register(User user);
}
