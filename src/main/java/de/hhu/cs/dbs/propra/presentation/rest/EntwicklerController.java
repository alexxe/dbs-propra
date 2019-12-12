package de.hhu.cs.dbs.propra.presentation.rest;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class EntwicklerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("entwickler")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/entwickler?land=xyz
    public List<Map<String, Object>> getGames(@QueryParam("land") String land) throws SQLException {

        Connection connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();

        return null;
    }

    @Path("entwickler/{entwicklerid}")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/spiele/spielid
    public Map<String, Object> getGame(@PathParam("entwicklerid") Integer entwicklerid) throws SQLException {
        Connection connection = dataSource.getConnection();
        return null;
    }
}
