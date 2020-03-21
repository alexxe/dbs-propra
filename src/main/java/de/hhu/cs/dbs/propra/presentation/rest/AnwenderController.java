package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.exceptions.APIError;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
public class AnwenderController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @Path("veranstalter")
    @POST // POST http://localhost:8080/veranstalter
    public Response CreateVeranstalter(@FormDataParam("email") String email, @FormDataParam("passwort") String passwort, @FormDataParam("vorname") String vorname, @FormDataParam("nachname") String nachname, @FormDataParam("veranstaltername") String veranstaltername) throws SQLException {
        if (!StringUtils.isNotBlank(email)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("email")).build();
        if (!StringUtils.isNotBlank(passwort))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("passwort")).build();
        if (!StringUtils.isNotBlank(vorname)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("vorname")).build();
        if (!StringUtils.isNotBlank(nachname)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("nachname")).build();
        if (!StringUtils.isNotBlank(veranstaltername)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("veranstaltername")).build();


        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        // Create User
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO User(Mailadresse, Passwort, Vorname, Nachname) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, passwort);
            preparedStatement.setObject(3, vorname);
            preparedStatement.setObject(4, nachname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        // Create Spiel
        try {
            stringStatement = "INSERT INTO Veranstalter(User_Mailadresse, Name) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, veranstaltername);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/veranstalter/" + email).build()).build();


    }

    @Path("veranstalter/{veranstalterid}")
    @GET // GET http://localhost:8080/veranstalter/123
    public Response getVeranstalterById(@PathParam("veranstalterid") String veranstalterid) throws SQLException {
        if (veranstalterid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("veranstalterid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  V.User_Mailadresse ='" + veranstalterid + "' ";

            String query = "SELECT U.Mailadresse, U.Vorname , U.Nachname, V.Name " +
                    "FROM Veranstalter V  INNER JOIN User U on V.User_Mailadresse = U.Mailadresse " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("email", resultSet.getObject(1));
                entity.put("vorname", resultSet.getObject(2));
                entity.put("nachname", resultSet.getObject(3));
                entity.put("name", resultSet.getObject(4));
                result = entity;
            }
            resultSet.close();
            connection.close();
            if (result == null) {
                throw new NotFoundException("Resource veranstalterid '" + veranstalterid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("kuenstler")
    @POST // POST http://localhost:8080//kuenstler
    public Response CreateKuenstler(@FormDataParam("email") String email, @FormDataParam("passwort") String passwort, @FormDataParam("vorname") String vorname, @FormDataParam("nachname") String nachname, @FormDataParam("kuenstlername") String kuenstlername) throws SQLException {
        if (!StringUtils.isNotBlank(email)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("email")).build();
        if (!StringUtils.isNotBlank(passwort))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("passwort")).build();
        if (!StringUtils.isNotBlank(vorname)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("vorname")).build();
        if (!StringUtils.isNotBlank(nachname)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("nachname")).build();
        if (!StringUtils.isNotBlank(kuenstlername)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("kuenstlername")).build();


        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        // Create User
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO User(Mailadresse, Passwort, Vorname, Nachname) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, passwort);
            preparedStatement.setObject(3, vorname);
            preparedStatement.setObject(4, nachname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        // Create Spiel
        try {
            stringStatement = "INSERT INTO Kuenstler(User_Mailadresse, kuenstlername) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, kuenstlername);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/kuenstler/" + email).build()).build();


    }

    @Path("kuenstler/{kuenstlerid}")
    @GET // GET http://localhost:8080/kuenstler/123
    public Response getKuenstlerById(@PathParam("kuenstlerid") String kuenstlerid) throws SQLException {
        if (kuenstlerid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("kuenstlerid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  K.User_Mailadresse ='" + kuenstlerid + "' ";

            String query = "SELECT U.Mailadresse, U.Vorname , U.Nachname, K.Kuenstlername " +
                    "FROM Kuenstler K  INNER JOIN User U on K.User_Mailadresse = U.Mailadresse " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("email", resultSet.getObject(1));
                entity.put("vorname", resultSet.getObject(2));
                entity.put("nachname", resultSet.getObject(3));
                entity.put("name", resultSet.getObject(4));
                result = entity;
            }
            resultSet.close();
            connection.close();
            if (result == null) {
                throw new NotFoundException("Resource kuenstlerid '" + kuenstlerid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("besucher")
    @POST // POST http://localhost:8080///besucher
    public Response CreateBesucher(@FormDataParam("email") String email, @FormDataParam("passwort") String passwort, @FormDataParam("vorname") String vorname, @FormDataParam("nachname") String nachname, @FormDataParam("geburtsdatum") String geburtsdatum, @FormDataParam("telefonnummer") String telefonnummer) throws SQLException {
        if (!StringUtils.isNotBlank(email)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("email")).build();
        if (!StringUtils.isNotBlank(passwort))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("passwort")).build();
        if (!StringUtils.isNotBlank(vorname)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("vorname")).build();
        if (!StringUtils.isNotBlank(nachname )) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("nachname")).build();
        if (!StringUtils.isNotBlank(geburtsdatum)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("geburtsdatum")).build();
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(geburtsdatum);
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("geburtsdatum")).build();
        }

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        // Create User
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO User(Mailadresse, Passwort, Vorname, Nachname) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, passwort);
            preparedStatement.setObject(3, vorname);
            preparedStatement.setObject(4, nachname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        // Create Besucher
        try {
            stringStatement = "INSERT INTO Besucher(User_Mailadresse, Geburtsdatum, Telefonnummer) values(?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, geburtsdatum);
            preparedStatement.setObject(3, telefonnummer);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/besucher/" + email).build()).build();


    }

    @Path("besucher/{besucherid}")
    @GET // GET http://localhost:8080/kuenstler/123
    public Response getBesucherById(@PathParam("besucherid") String besucherid) throws SQLException {
        if (besucherid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("besucherid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  B.User_Mailadresse ='" + besucherid + "' ";

            String query = "SELECT U.Mailadresse, U.Vorname , U.Nachname, B.Geburtsdatum, B.Telefonnummer " +
                    "FROM Besucher B  INNER JOIN User U on B.User_Mailadresse = U.Mailadresse " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("email", resultSet.getObject(1));
                entity.put("vorname", resultSet.getObject(2));
                entity.put("nachname", resultSet.getObject(3));
                entity.put("geburtsdatum", resultSet.getObject(4));
                entity.put("telefonnummer", resultSet.getObject(5));
                result = entity;
            }
            resultSet.close();
            connection.close();
            if (result == null) {
                throw new NotFoundException("Resource besucherid '" + besucherid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }
}
