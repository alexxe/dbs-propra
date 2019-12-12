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
public class ExampleController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @GET // GET http://localhost:8080
    public String halloWelt() {
        return "Hallo Welt!";
    }

    @Path("foo")
    @RolesAllowed({"USER", "EMPLOYEE", "ADMIN"})
    @GET
    // GET http://localhost:8080/foo => OK, wenn Benutzer die Rolle "USER", "EMPLOYEE" oder "ADMIN" hat. Siehe SQLiteUserRepository.
    public String halloFoo() {
        return "Hallo " + securityContext.getUserPrincipal() + "!";
    }

    @Path("foo2/{bar}")
    @GET // GET http://localhost:8080/foo/xyz
    public String halloFoo2(@PathParam("bar") String bar) {
        if (!bar.equals("foo")) throw new NotFoundException("Resource '" + bar + "' not found");
        return "Hallo " + bar + "!";
    }

    @Path("foo3")
    @GET // GET http://localhost:8080/foo3?bar=xyz
    public String halloFoo3(@QueryParam("bar") String bar) {
        return "Hallo " + bar + "!";
    }

    @Path("bar")
    @GET // GET http://localhost:8080/bar => Bad Request; http://localhost/bar?foo=xyz => OK
    public Response halloBar(@QueryParam("foo") String foo) {
        if (foo == null) throw new BadRequestException();
        return Response.status(Response.Status.OK).entity("Hallo Bar!").build();
    }

    @Path("bar2")
    @GET // GET http://localhost:8080/bar2
    public List<Map<String, Object>> halloBar2(@QueryParam("name") @DefaultValue("Max Mustermann") List<String> names) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT ?;");
        preparedStatement.closeOnCompletion();
        int random = ThreadLocalRandom.current().nextInt(0, names.size());
        preparedStatement.setObject(1, names.get(random));
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            entity.put("name", resultSet.getObject(1));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("foo")
    @POST // POST http://localhost:8080/foo
    public Response einUpload(@FormDataParam("name") String name, @FormDataParam("file") InputStream file) {
        if (name == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (file == null) return Response.status(Response.Status.BAD_REQUEST).build();
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(file);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.created(uriInfo.getAbsolutePathBuilder().path("234235").build()).build();
    }

    @Path("spiele")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/spiele?name=xyz
    public List<Map<String, Object>> getGames(@QueryParam("name") String name, @QueryParam("fsk") String fsk, @QueryParam("top") Integer top, @QueryParam("entwicklerid") String entwicklerid) throws SQLException {
        Principal user = securityContext.getUserPrincipal();
        Connection connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();
        String where = "";
        if (name != null) {
            where = where + " P.Name  LIKE '%" + name + "%' ";
        }
        if (fsk != null) {
            if (where.length() > 0){
                where = where + " AND ";
            }
            where = where + " S.FSK  <= " + fsk + " ";
        }
        if (entwicklerid != null) {
            if (where.length() > 0){
                where = where + " AND ";
            }
            where = where + " E.User_Mailadresse  = " + entwicklerid + " ";
        }

        if (where.length() > 0) {
            where = " WHERE " + where;
        }

        String orderBy = " ORDER BY P.name ";
        String limit = "";
        if (top != null) {
            orderBy = " ORDER BY Note DESC ";
            limit = " LIMIT " + top + " ";
        }


        String query = "SELECT S.Produkt_ID, P.Name , S.FSK, avg(B.Schulnote) AS Note, E.User_Mailadresse, P.ID" +
                " FROM Spiel S INNER JOIN Produkt P ON S.Produkt_ID = P.ID INNER JOIN Entwickler E " +
                " ON P .Entwickler_User_Mailadresse = E.User_Mailadresse LEFT JOIN bewertet B ON P.ID = B.Produkt_ID " + where +
                " GROUP BY S.Produkt_ID, P.Name , S.FSK, E.User_Mailadresse, P.ID " + orderBy + limit;
        ResultSet resultSet = stmt.executeQuery(query);
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            entity.put("spielid", resultSet.getObject(1));
            entity.put("name", resultSet.getObject(2));
            entity.put("fsk", resultSet.getObject(3));
            entity.put("durchschnittsbewertung", resultSet.getObject(4));
            entity.put("entwicklerid", resultSet.getObject(5));
            entity.put("produktid", resultSet.getObject(6));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        return entities;
    }

    @Path("spiele/{spielid}")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/spiele/spielid
    public Map<String, Object> getGame(@PathParam("spielid") Integer spielid) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT S.Produkt_ID, P.Name , P.Beschreibung, P.Bild, P.Datum, S.FSK, N.Spiel_Produkt_ID1 , count(Z.Spiel_Produkt_ID) , E.User_Mailadresse, P.ID " +
                "FROM Spiel S INNER JOIN Produkt P ON S.Produkt_ID = P.ID INNER JOIN Entwickler E  ON P .Entwickler_User_Mailadresse = E.User_Mailadresse " +
                "LEFT JOIN Nachfolger_von N ON N.Spiel_Produkt_ID2 = S.Produkt_ID LEFT JOIN Zusatzinhalt Z ON Z.Spiel_Produkt_ID = S.Produkt_ID   " +
                "WHERE  S.Produkt_ID = ?" +
                "GROUP BY S.Produkt_ID, P.Name , P.Beschreibung, P.Bild, P.Datum, S.FSK, N.Spiel_Produkt_ID1 , E.User_Mailadresse, P.ID ");
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, spielid);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Object> entity;
        while (resultSet.next()) {
            entity = new HashMap<>();
            entity.put("spielid", resultSet.getObject(1));
            entity.put("name", resultSet.getObject(2));
            entity.put("beschreibung", resultSet.getObject(3));
            entity.put("bild", resultSet.getObject(4));
            entity.put("datum", resultSet.getObject(5));
            entity.put("fsk", resultSet.getObject(6));
            entity.put("vorgaengerid", resultSet.getObject(7));
            entity.put("zusatzinhalte", resultSet.getObject(8));
            entity.put("entwicklerid", resultSet.getObject(9));
            entity.put("produktid", resultSet.getObject(10));
            entities.add(entity);
        }
        resultSet.close();
        connection.close();
        if (entities.size() > 0) {
            return entities.get(0);
        }

        throw new NotFoundException("Resource '" + spielid + "' not found");
    }
}
