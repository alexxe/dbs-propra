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
public class SpieleController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("spiele")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/spiele?name=xyz
    public List<Map<String, Object>> getGames(@QueryParam("name") String name, @QueryParam("fsk") String fsk, @QueryParam("top") Integer top, @QueryParam("entwicklerid") String entwicklerid) throws Exception {
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String where = "";
            if (name != null) {
                where = where + " P.Name  LIKE '%" + name + "%' ";
            }
            if (fsk != null) {
                if (where.length() > 0) {
                    where = where + " AND ";
                }
                where = where + " S.FSK  <= " + fsk + " ";
            }
            if (entwicklerid != null) {
                if (where.length() > 0) {
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
        } catch (SQLException ex) {
            throw  new BadRequestException(ex.getMessage());
        }
    }

    @Path("spiele/{spielid}")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/spiele/spielid
    public Object getGame(@PathParam("spielid") Integer spielid) throws Exception {
        try {
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
        } catch (SQLException ex) {
            throw  new BadRequestException(ex.getMessage());
        }
        throw new NotFoundException("Resource '" + spielid + "' not found");
    }


    @POST
    @Path("test")
    public Response Test(@FormDataParam("name") String name) {
        return Response.created(UriBuilder.fromUri("http://localhost:8080/spiele/").build()).build();
    }

    @Path("spiele")
    @RolesAllowed({"ADMIN"})
    @POST // POST http://localhost:8080/spiele
    public Response CreateGame(@FormDataParam("name") String name, @FormDataParam("beschreibung") String beschreibung, @FormDataParam("bild") String bild, @FormDataParam("datum") String datum, @FormDataParam("fsk") String fsk, @FormDataParam("vorgaengerid") Integer vorgaengerid, @FormDataParam("entwicklerid") String entwicklerid) {
        if (name == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (beschreibung == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (bild == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (datum == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (fsk == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (entwicklerid == null) return Response.status(Response.Status.BAD_REQUEST).build();

        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            // Create Produkt
            int new_id;
            String stringStatement = null;
            PreparedStatement preparedStatement = null;
            try {
                stringStatement = "INSERT INTO Produkt(Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) values(?,?,?,?,?);";
                preparedStatement = connection.prepareStatement(stringStatement);
                preparedStatement.closeOnCompletion();
                preparedStatement.setObject(1, name);
                preparedStatement.setObject(2, bild);
                preparedStatement.setObject(3, beschreibung);
                preparedStatement.setObject(4, datum);
                preparedStatement.setObject(5, securityContext.getUserPrincipal().getName());
                preparedStatement.executeUpdate();
                new_id = preparedStatement.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
            // Create Spiel
            try {
                stringStatement = "INSERT INTO Spiel(Produkt_ID, FSK) values(?,?);";
                preparedStatement = connection.prepareStatement(stringStatement);
                preparedStatement.closeOnCompletion();
                preparedStatement.setObject(1, new_id);
                preparedStatement.setObject(2, fsk);
                preparedStatement.executeUpdate();
                new_id = preparedStatement.getGeneratedKeys().getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
            connection.commit();
            connection.close();
            return Response.created(UriBuilder.fromUri("http://localhost:8080/spiele/"+new_id).build()).build();


        } catch (SQLException ex) {
            throw  new BadRequestException(ex.getMessage());
        }
    }
}
