package it.eda.users.ui.api;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/")

public class BaseApi {

    @Inject
    Template index;
    @Inject
    Template user;

    @Inject
    Template admin;

    /**
     * Gets the user page.
     *
     * This method is used to retrieve the HTML representation of the user page.
     *
     * @return A Response object containing the rendered user page.
     */
    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_HTML)
    public Response getUserPage() {
        TemplateInstance templateInstance = user.instance();
        return Response.ok(templateInstance.render()).build();
    }

    /**
     * Retrieves the HTML representation of the login page.
     *
     * This method is used to retrieve the HTML representation of the login page.
     *
     * @return A Response object containing the rendered login page.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response login() {
        TemplateInstance templateInstance = index.instance();
        return Response.ok(templateInstance.render()).build();

    }


    @GET
    @Path("/admin")
    @Produces(MediaType.TEXT_HTML)
    public Response admin() {
        TemplateInstance templateInstance = admin.instance();
        return Response.ok(templateInstance.render()).build();

    }

}

