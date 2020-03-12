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
public class FestivalsController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("festivals")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/festivals?bezeichnung=xyz
    public List<Map<String, Object>> getFastivals(@QueryParam("bezeichnung") String bezeichnung) throws SQLException {

        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            String where = "";
            if (bezeichnung != null) {
                where = where + " P.Name  LIKE '%" + bezeichnung + "%' ";
            }

            if (where.length() > 0) {
                where = " WHERE " + where;
            }

            String orderBy = " ORDER BY P.name ";



            String query = "SELECT F.ID, F.Bezeichnung , F.DAtum, F.bild" +
                    " FROM Festival F ";
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

    @Path("entwickler/{entwicklerid}")
    @RolesAllowed({"USER"})
    @GET // GET http://localhost:8080/entwickler/entwicklerid
    public Map<String, Object> getEntwicklerById(@PathParam("entwicklerid") String entwicklerid) throws SQLException {
        Connection connection = dataSource.getConnection();
        return null;
    }
}
