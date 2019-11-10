package com.manywho.services.sql.suites.common.controllers.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;

public class LoadWithoutOrderBy extends ServiceFunctionalTest {

    @Before
    public void setupDatabase() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");

        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("city") +
                    "(" +
                    "cityname character varying(255)," +
                    "countryname character varying(255)," +
                    "description character varying(1024)," +
                    "CONSTRAINT city_pk PRIMARY KEY (cityname, countryname)" +
                    ");";
            connection.createQuery(sql).executeUpdate();
        }
    }

    //SQL Server will throw an exception if there isn't order by and there is a limit (we force limit if there isn't any)
    @Test
    public void testLoadDataWithoutOrderBy() throws Exception {

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Montevideo', 'Uruguay');";
            String sq2 = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('London', 'England');";
            String sq3 = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Paris', 'France');";
            String sq4 = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Madrid', 'Spain');";
            String sq5 = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Rome', 'Italy');";

            connection.createQuery(sql).executeUpdate();
            connection.createQuery(sq2).executeUpdate();
            connection.createQuery(sq3).executeUpdate();
            connection.createQuery(sq4).executeUpdate();
            connection.createQuery(sq5).executeUpdate();
        }

        MockHttpResponse response = DefaultApiRequest.loadDataRequestResponse("/data",
                "suites/common/data/load/by-order-by/load-request.json",
                configurationParameters(),
                dispatcher
        );

        JSONObject jsonObject = new JSONObject(response.getContentAsString());
        Assert.assertEquals(2, ((JSONArray)jsonObject.get("objectData")).length());
    }


    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("city", connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
