package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.exceptions.APIError;
import org.apache.commons.lang3.StringUtils;
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
        if (!StringUtils.isNotBlank(name)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("name")).build();
        if (gruendungsjahr == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("gruendungsjahr")).build();
        if (genreid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("genreid")).build();

        Connection connection = dataSource.getConnection();
        {
            String query = "SELECT ID FROM Genre WHERE ID = '" + genreid + "'";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            if (!resultSet.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity(new APIError("genreid")).build();
            }

        }
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

    @Path("bands/{bandid}/kuenstler")
    @RolesAllowed({"KUENSTLER"})
    @POST // POST http://localhost:8080/bands/123
    public Response AddKuenstlerToBand(@PathParam("bandid") Integer bandid, @FormDataParam("kuenstlerid") String kuenstlerid) throws SQLException {
        if (bandid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bandid")).build();
        if (!StringUtils.isNotBlank(kuenstlerid))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("kuenstlerid")).build();
        try {
            Connection connection = dataSource.getConnection();
            {
                String query = "SELECT ID FROM Band WHERE ID = '" + bandid + "'";
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity(new APIError("bandid")).build();
                }

            }
            {
                String query = "SELECT User_Mailadresse FROM Kuenstler WHERE User_Mailadresse = '" + kuenstlerid + "'";
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity(new APIError("kuenstlerid")).build();
                }

            }
            {
                String query = "SELECT H.Band_ID FROM hat H  WHERE H.BAND_ID = " + bandid + " AND H.Kuenstler_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' ";

                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.FORBIDDEN).entity(new APIError("bandid")).build();
                }

            }

        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            Connection connection = dataSource.getConnection();
            stringStatement = "INSERT INTO hat(BAND_ID, Kuenstler_User_Mailadresse) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bandid);
            preparedStatement.setObject(2, kuenstlerid);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }


        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("bands/{bandid}/genres")
    @RolesAllowed({"KUENSTLER"})
    @POST // POST http://localhost:8080/bands/123
    public Response AddGenreToBand(@PathParam("bandid") Integer bandid, @FormDataParam("genreid") Integer genreid) throws SQLException {
        if (bandid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bandid")).build();
        if (genreid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("genreid")).build();
        try {
            Connection connection = dataSource.getConnection();
            {
                String query = "SELECT ID FROM Band WHERE ID = '" + bandid + "'";
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity(new APIError("bandid")).build();
                }

            }
            {
                String query = "SELECT ID FROM Genre WHERE ID = '" + genreid + "'";
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity(new APIError("genreid")).build();
                }

            }
            {
                String query = "SELECT H.Band_ID FROM hat H  WHERE H.BAND_ID = " + bandid + " AND H.Kuenstler_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' ";

                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                if (!resultSet.next()) {
                    return Response.status(Response.Status.FORBIDDEN).entity(new APIError("bandid")).build();
                }

            }

        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            Connection connection = dataSource.getConnection();
            stringStatement = "INSERT INTO gehoert_zu(Genre_ID, Band_ID) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, genreid);
            preparedStatement.setObject(2, bandid);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }


        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
