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
public class BesucherController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @Path("tickets")
    @RolesAllowed({"BESUCHER"})
    @GET // GET http://localhost:8080/tickets
    public List<Map<String, Object>> getTickets(@QueryParam("f_bezeichnung") String f_bezeichnung, @QueryParam("vip") Boolean vip, @QueryParam("preis") String preis) throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            String where = "T.Besucher_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() +"' ";
            if (f_bezeichnung != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " F.Bezeichnung  LIKE '%" + f_bezeichnung + "%' ";
            }
            if (vip != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " T.VIP_Vermerk  = " + vip + " ";
            }
            if (preis != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " T.Preis  >= " + preis + " ";
            }

            if (where.length() > 0) {
                where = " WHERE " + where;
            }

            String orderBy = " ORDER BY F.datum ";

            String query = "SELECT T.ID, T.Preis , date(T.Datum) AS Date, time(T.Datum) AS Time, T.VIP_Vermerk " +
                    "FROM Ticket T  INNER JOIN Festival F ON F.ID = T.Festival_ID " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                entity.put("ticketid", resultSet.getObject(1));
                entity.put("preis", resultSet.getObject(2));

                LocalDateTime dt = LocalDateTime.of(LocalDate.parse(resultSet.getObject(3).toString()), LocalTime.parse(resultSet.getObject(4).toString()));
                ZonedDateTime zdt = ZonedDateTime.of(dt, ZoneId.of("UTC"));
                entity.put("datum", zdt.format(DateTimeFormatter.ISO_INSTANT));
                entity.put("vip", resultSet.getObject(5));
                entities.add(entity);
            }
            resultSet.close();
            connection.close();

            return entities;
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("festivals/{festivalid}/tickets")
    @RolesAllowed({"BESUCHER"})
    @POST // POST http://localhost:8080////festivals/{festivalid}/tickets
    public Response CreateTicket(@PathParam("festivalid") Integer festivalid, @FormDataParam("preis") Double preis, @FormDataParam("datum") String datum, @FormDataParam("vip") Boolean vip) throws SQLException {
        if (festivalid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        if (preis == null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("preis")).build();
        if (!StringUtils.isNotBlank(datum)) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
        if (vip == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("vip")).build();

        try {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datum);
        } catch (ParseException e) {
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(datum);
            } catch (ParseException ex) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("datum")).build();
            }

        }

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        // Create User
        int new_id;
        String stringStatement = null;
        PreparedStatement preparedStatement = null;
        try {
            stringStatement = "INSERT INTO Ticket(Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) values(?,?,?,?,?);";
            preparedStatement = connection.prepareStatement(stringStatement);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, preis);
            preparedStatement.setObject(2, datum);
            preparedStatement.setObject(3, vip);
            preparedStatement.setObject(4, festivalid);
            preparedStatement.setObject(5, securityContext.getUserPrincipal().getName());
            preparedStatement.executeUpdate();
            new_id = preparedStatement.getGeneratedKeys().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }

        connection.commit();
        connection.close();
        return Response.created(UriBuilder.fromUri("http://localhost:8080/tickets/" + new_id).build()).build();


    }

    @Path("tickets/{ticketid}")
    @RolesAllowed({"BESUCHER"})
    @GET // GET http://localhost:8080/tickets/123
    public Response getTicketById(@PathParam("ticketid") String ticketid) throws SQLException {
        if (ticketid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("ticketid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  T.ID = " + ticketid + " AND T.Besucher_User_Mailadresse = '" + securityContext.getUserPrincipal().getName() + "' ";

            String query = "SELECT T.Preis, T.Datum , T.VIP_Vermerk, T.Festival_ID " +
                    "FROM Ticket T  " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            HashMap result = null;
            while (resultSet.next()) {
                HashMap entity = new HashMap<>();
                entity.put("preis", resultSet.getObject(1));
                entity.put("datum", resultSet.getObject(2));
                entity.put("vip", resultSet.getObject(3));
                entity.put("festivalid", resultSet.getObject(4));
                result = entity;
            }
            resultSet.close();
            connection.close();
            if (result == null) {
                throw new NotFoundException("Resource ticketid '" + ticketid + "' not found");
            }
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("tickets/{ticketid}")
    @RolesAllowed({"BESUCHER"})
    @DELETE // DELETE http://localhost:8080/tickets/123
    public Response deleteTicket(@PathParam("ticketid") Integer ticketid) throws SQLException {
        if (ticketid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("ticketid")).build();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Ticket  WHERE ID = ? AND Besucher_User_Mailadresse = ? ");
            preparedStatement.closeOnCompletion();
            preparedStatement.setInt(1, ticketid);
            preparedStatement.setObject(2, securityContext.getUserPrincipal().getName());
            Integer row = preparedStatement.executeUpdate();
            connection.close();
            if (row == 0) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }
}
