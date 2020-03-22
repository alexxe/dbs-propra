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
import java.util.HashMap;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class VeranstalterController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("orte")
    @RolesAllowed({"VERANSTALTER"})
    @POST // POST http://localhost:8080/orte
    public Response CreateOrt(@FormDataParam("bezeichnung") String bezeichnung, @FormDataParam("land") String land) throws SQLException {
        if (!StringUtils.isNotBlank(bezeichnung))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bezeichnung")).build();
        if (!StringUtils.isNotBlank(land))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("land")).build();


        Connection connection = dataSource.getConnection();
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO Ort(Bezeichnung, Land) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bezeichnung);
            preparedStatement.setObject(2, land);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/orte/" + new_id).build()).build();
    }

    @Path("orte/{ortid}")
    @RolesAllowed({"VERANSTALTER"})
    @GET // GET http://localhost:8080/orte/abc
    public Response getOrt(@PathParam("ortid") Integer ortid) throws SQLException {
        if (ortid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("ortid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String query = "SELECT O.Bezeichnung, O.Land FROM Ort O  WHERE O.ID = " + ortid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("bezeichnung", resultSet.getObject(1));
                entity.put("land", resultSet.getObject(2));
                result = entity;
            }
            resultSet.close();
            connection.close();

            if (result == null) {
                throw new NotFoundException("Resource ortid '" + ortid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("festivals")
    @RolesAllowed({"VERANSTALTER"})
    @POST // POST http://localhost:8080/festivals
    public Response CreateFestival(@FormDataParam("bezeichnung") String bezeichnung, @FormDataParam("datum") String datum, @FormDataParam("bild") String bild, @FormDataParam("ortid") Integer ortid) throws SQLException {
        if (!StringUtils.isNotBlank(bezeichnung))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bezeichnung")).build();
        if (!StringUtils.isNotBlank(datum))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
        if (!StringUtils.isNotBlank(bild))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bild")).build();
        if (ortid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("ortid")).build();

        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(datum);
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
        }

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO Festival(Bezeichnung, Bild, Datum, Ort_ID) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bezeichnung);
            preparedStatement.setObject(2, bild);
            preparedStatement.setObject(3, datum);
            preparedStatement.setObject(4, ortid);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }

        try {
            stringStatement = "INSERT INTO organisiert(Veranstalter_User_Mailadresse, Festival_ID) values(?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, securityContext.getUserPrincipal().getName());
            preparedStatement.setObject(2, new_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }

        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/festivals/" + new_id).build()).build();
    }

    @Path("festivals/{festivalid}")
    @RolesAllowed({"VERANSTALTER"})
    @GET // GET http://localhost:8080/festivals/123
    public Response getFestivalById(@PathParam("festivalid") Integer festivalid) throws SQLException {
        if (festivalid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String query = "SELECT F.Bezeichnung, F.Bild, F.Datum FROM Festival F  WHERE F.ID = " + festivalid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("bezeichnung", resultSet.getObject(1));
                entity.put("bild", resultSet.getObject(2));
                entity.put("datum", resultSet.getObject(3));
                result = entity;
            }
            resultSet.close();
            connection.close();

            if (result == null) {
                throw new NotFoundException("Resource festivalid '" + festivalid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("festivals/{festivalid}/buehnen")
    @RolesAllowed({"VERANSTALTER"})
    @POST // POST http://localhost:8080/festivals/{festivalid}/buehnen
    public Response AddBueneToFestival(@PathParam("festivalid") String festivalid, @FormDataParam("name") String name, @FormDataParam("sitzplaetze") Integer sitzplaetze, @FormDataParam("stehplaetze") Integer stehplaetze) throws SQLException {
        if (!StringUtils.isNotBlank(festivalid))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        if (!StringUtils.isNotBlank(name))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("name")).build();
        if (sitzplaetze == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("sitzplaetze")).build();
        if (stehplaetze == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("stehplaetze")).build();

        Connection connection = dataSource.getConnection();
        String stringStatement = null;
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT O.Festival_ID FROM organisiert O  WHERE O.Veranstalter_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' AND O.Festival_ID = " + festivalid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("Festival_ID", resultSet.getObject(1));
                result = entity;
            }
            resultSet.close();


            if (result == null) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        //connection.setAutoCommit(false);
        int new_id;

        try {
            stringStatement = "INSERT INTO Buehne(Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, name);
            preparedStatement.setObject(2, sitzplaetze);
            preparedStatement.setObject(3, stehplaetze);
            preparedStatement.setObject(4, festivalid);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            //connection.rollback();
            throw e;
        }


        //connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/buehnen/" + new_id).build()).build();
    }

    @Path("buehnen/{buehneid}")
    @RolesAllowed({"VERANSTALTER"})
    @GET // GET http://localhost:8080/buehnen/123
    public Response getFestivalById(@PathParam("buehneid") String buehneid) throws SQLException {
        if (buehneid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("buehneid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String query = "SELECT B.Bezeichnung, B.Sitzplaetze, B.Stehplaetze FROM Buehne B  WHERE B.ID = " + buehneid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("bezeichnung", resultSet.getObject(1));
                entity.put("sitzplaetze", resultSet.getObject(2));
                entity.put("stehplaetze", resultSet.getObject(3));
                result = entity;
            }
            resultSet.close();
            connection.close();

            if (result == null) {
                throw new NotFoundException("Resource buehneid '" + buehneid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("festivals/{festivalid}/buehnen/{buehneid}/programmpunkte")
    @RolesAllowed({"VERANSTALTER"})
    @POST // POST http://localhost:8080/festivals/{festivalid}/buehnen
    public Response AddProgrammpunktToBuene(@PathParam("festivalid") String festivalid, @PathParam("buehneid") String buehneid, @FormDataParam("startzeitpunkt") String startzeitpunkt, @FormDataParam("dauer") Integer dauer, @FormDataParam("bandid") Integer bandid) throws SQLException {
        if (!StringUtils.isNotBlank(festivalid))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        if (!StringUtils.isNotBlank(buehneid))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("buehneid")).build();
        if (!StringUtils.isNotBlank(startzeitpunkt))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("startzeitpunkt")).build();
        if (dauer == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("dauer")).build();
        if (bandid == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bandid")).build();

        try {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startzeitpunkt);
        } catch (ParseException e) {
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(startzeitpunkt);
            } catch (ParseException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("startzeitpunkt")).build();
            }

        }

        Connection connection = dataSource.getConnection();
        String stringStatement = null;
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT O.Festival_ID FROM organisiert O  WHERE O.Veranstalter_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' AND O.Festival_ID = " + festivalid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("Festival_ID", resultSet.getObject(1));
                result = entity;
            }
            resultSet.close();


            if (result == null) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        //connection.setAutoCommit(false);
        int new_id;

        try {
            stringStatement = "INSERT INTO Programmpunkt(Uhrzeit, Dauer, Buehne_ID, Band_ID) values(?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, startzeitpunkt);
            preparedStatement.setObject(2, dauer);
            preparedStatement.setObject(3, buehneid);
            preparedStatement.setObject(4, bandid);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            //connection.rollback();
            throw e;
        }


        //connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/programmpunkt/" + new_id).build()).build();
    }

    @Path("festivals/{festivalid}")
    @RolesAllowed({"VERANSTALTER"})
    @PATCH // PATCH http://localhost:8080/festivals/123
    public Response UpdateFestival(@PathParam("festivalid") String festivalid, @FormDataParam("bezeichnung") String bezeichnung, @FormDataParam("datum") String datum, @FormDataParam("bild") String bild) throws SQLException {
        if (!StringUtils.isNotBlank(festivalid))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        if (!StringUtils.isNotBlank(bezeichnung))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bezeichnung")).build();
        if (!StringUtils.isNotBlank(datum))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
        if (!StringUtils.isNotBlank(bild))
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("bild")).build();


        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(datum);
        } catch (ParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
        }


        Connection connection = dataSource.getConnection();
        String stringStatement = null;
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT O.Festival_ID FROM organisiert O  WHERE O.Veranstalter_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' AND O.Festival_ID = " + festivalid + " ";
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("Festival_ID", resultSet.getObject(1));
                result = entity;
            }
            resultSet.close();


            if (result == null) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        //connection.setAutoCommit(false);
        int new_id;

        try {
            stringStatement = "UPDATE Festival SET Bezeichnung = ?, Datum = ?, Bild = ? WHERE ID = " + festivalid + " ";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, bezeichnung);
            preparedStatement.setObject(2, datum);
            preparedStatement.setObject(3, bild);
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            //connection.rollback();
            throw e;
        }


        //connection.commit();
        connection.close();
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
