package de.hhu.cs.dbs.propra.presentation.rest;

import de.hhu.cs.dbs.propra.application.exceptions.APIError;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class FestivalsController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("festivals")
    @GET // GET http://localhost:8080/festivals?bezeichnung=xyz
    public List<Map<String, Object>> getFastivals(@QueryParam("bezeichnung") String bezeichnung, @QueryParam("ort") String ort, @QueryParam("veranstalter") String veranstalter, @QueryParam("jahr") Integer jahr) throws SQLException {

        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String where = "";
            if (bezeichnung != null) {
                where = where + " F.Bezeichnung  LIKE '%" + bezeichnung + "%' ";
            }
            if (ort != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " ORT.Bezeichnung  = '" + ort + "' ";
            }
            if (veranstalter != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " V.Name  = '" + veranstalter + "' ";
            }
            if (jahr != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " substr(F.Datum, 1, 4)  = '" + jahr + "' ";
            }
            if (where.length() > 0) {
                where = " WHERE " + where;
            }

            String orderBy = " ORDER BY F.datum ";


            String query = "SELECT F.ID, F.Bezeichnung , F.Datum, F.bild" +
                    " FROM Festival F INNER JOIN organisiert O ON F.ID = O.Festival_ID INNER JOIN Veranstalter V ON V.User_Mailadresse = O.Veranstalter_User_Mailadresse INNER JOIN Ort ORT ON F.Ort_ID = ORT.ID " + where + orderBy;
            ResultSet resultSet = stmt.executeQuery(query);
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                entity.put("festivalid", resultSet.getObject(1));
                entity.put("bezeichnung", resultSet.getObject(2));
                entity.put("datum", resultSet.getObject(3));
                entity.put("bild", resultSet.getObject(4));
                entities.add(entity);
            }
            resultSet.close();
            connection.close();
            return entities;
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Path("festivals/{festivalid}/buehnen")
    @GET // GET http://localhost:8080/festivals/123/buehnen
    public Object getBuenenByFestivalId(@PathParam("festivalid") Integer festivalid, @QueryParam("bezeichnung") String bezeichnung, @QueryParam("sitzplaetze") Integer sitzplaetze) throws SQLException {
        if (festivalid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  B.Festival_ID =" + festivalid + " ";
            if (bezeichnung != null) {
                where = where + " AND B.Bezeichnung  LIKE '%" + bezeichnung + "%' ";
            }
            if (sitzplaetze != null) {
                where = where + " AND B.sitzplaetze  >= " + sitzplaetze;
            }

            String query = "SELECT B.ID, B.Festival_ID , B.Bezeichnung, B.Sitzplaetze, B.Stehplaetze " +
                    "FROM Buehne B  " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                entity.put("buehneid", resultSet.getObject(1));
                entity.put("festivalid", resultSet.getObject(2));
                entity.put("name", resultSet.getObject(3));
                entity.put("sitzplaetze", resultSet.getObject(4));
                entity.put("stehplaetze", resultSet.getObject(5));
                entities.add(entity);
            }
            resultSet.close();
            connection.close();
            if (bezeichnung == null && sitzplaetze == null && entities.size() == 0) {
                throw new NotFoundException("Resource festivalid '" + festivalid + "' not found");
            }
            return entities;
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("bands")
    @GET // GET http://localhost:8080/bands?
    public List<Map<String, Object>> getBands(@QueryParam("name") String name, @QueryParam("genre") String genre, @QueryParam("gruendungsjahr") Integer gruendungsjahr) throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            String where = "";
            if (name != null) {
                where = where + " B.Name  LIKE '%" + name + "%' ";
            }
            if (genre != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " GR.Name  = '" + genre + "' ";
            }
            if (gruendungsjahr != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " B.Gruendungsjahr  >= " + gruendungsjahr + " ";
            }

            if (where.length() > 0) {
                where = " WHERE " + where;
            }

            String orderBy = " ORDER BY F.datum ";

            String query = "SELECT DISTINCT B.ID, B.Name , B.Gruendungsjahr " +
                    "FROM Band B  INNER JOIN gehoert_zu GZ ON GZ.Band_ID = B.ID INNER JOIN Genre GR ON GR.ID = GZ.Genre_ID " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                entity.put("bandid", resultSet.getObject(1));
                entity.put("name", resultSet.getObject(2));
                entity.put("gruendungsjahr", resultSet.getObject(3));

                entities.add(entity);
            }
            resultSet.close();
            connection.close();

            return entities;
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }

    @Path("festivals/{festivalid}/buehnen/{buehneid}/programmpunkte")
    @GET // GET http://localhost:8080/festivals/{festivalid}/buehnen/{buehneid}/programmpunkte
    public Object getProgrammpunkte(@PathParam("festivalid") Integer festivalid, @PathParam("buehneid") Integer buehneid, @QueryParam("bandname") String bandname, @QueryParam("dauer") Integer dauer) throws SQLException {
        if (festivalid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("festivalid")).build();
        if (buehneid == null) return Response.status(Response.Status.BAD_REQUEST).entity(new APIError("buehneid")).build();
        try {
            Connection connection = dataSource.getConnection();
            String where = " WHERE  B.Festival_ID =" + festivalid + " AND P.Buehne_ID = " + buehneid + " ";
            if (bandname != null) {
                where = where + " AND BN.Name = '" + bandname + "' ";
            }
            if (dauer != null) {
                where = where + " AND P.Dauer  >= " + dauer;
            }

            String query = "SELECT F.Datum, P.Uhrzeit, P.Dauer " +
                    "FROM Programmpunkt P INNER JOIN  Buehne B on P.Buehne_ID = B.ID INNER JOIN  Band BN on P.Band_ID = BN.ID INNER JOIN  Festival F on F.ID = B.Festival_ID " + where;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();


                LocalDateTime dt = LocalDateTime.of(LocalDate.parse(resultSet.getObject(1).toString()), LocalTime.parse(resultSet.getObject(2).toString()));
                ZonedDateTime zdt = ZonedDateTime.of(dt, ZoneId.of("UTC"));
                entity.put("startzeitpunkt", zdt.format(DateTimeFormatter.ISO_INSTANT));
                entity.put("dauer", resultSet.getObject(3));
                entities.add(entity);
            }
            resultSet.close();
            connection.close();
            if (bandname == null && dauer == null && entities.size() == 0) {
                throw new NotFoundException("Resource festivalid '" + festivalid + "' buehneid '" + buehneid + " not found");
            }
            return entities;
        } catch (SQLException ex) {
            throw new BadRequestException(ex.getMessage());
        }

    }


}
