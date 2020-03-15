package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.exceptions.APIError;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class KuenstlerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("bands")
    @RolesAllowed({"KUENSTLER"})
    @POST // POST http://localhost:8080/bands
    public Response CreateBand(@FormDataParam("name") String name, @FormDataParam("gruendungsjahr") Integer gruendungsjahr, @FormDataParam("genreid") Integer genreid) throws SQLException {
        if (name == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("name")).build();
        if (gruendungsjahr == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("gruendungsjahr")).build();
        if (genreid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("genreid")).build();

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        // Create Band
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO Band(Name, Gruendungsjahr) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, gruendungsjahr);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        // Create gehoert_zu
        try {
            stringStatement = "INSERT INTO gehoert_zu(Genre_ID, Band_ID) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, genreid);
            preparedStatement.setObject(2, new_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/bands/" + new_id).build()).build();
    }

    @Path("bands/{bandid}")
    @RolesAllowed({"KUENSTLER"})
    @GET // GET http://localhost:8080/bands/123
    public Response getBandById(@PathParam("bandid") Integer bandid) throws SQLException {
        if (bandid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bandid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  B.ID = " + bandid + " ";
            String query = "SELECT B.Name , B.Gruendungsjahr " +
                    "FROM Band B  " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("name", resultSet.getObject(1));
                entity.put("gruendungsjahr", resultSet.getObject(2));
                result = entity;
            }
            resultSet.close();
            connection.close();

            if (result == null) {
                throw new NotFoundException("Resource bandid '" + bandid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }


}
