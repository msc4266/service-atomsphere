package com.manywho.services.sql.suites.postgresql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Test;
import org.sql2o.Connection;

import static junit.framework.TestCase.assertEquals;

public class DeleteTest extends ServiceFunctionalTest {
    @Test
    public void testDeleteDataByExternalIdWithAliases() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("postgresql");
        try (Connection connection = getSql2o().open()) {
            String sqlCreateTable = "CREATE TABLE " + escapeTableName("city") +
                    "(" +
                    "cityname character varying(255)," +
                    "countryname character varying(255)," +
                    "description character varying(1024)," +
                    "CONSTRAINT city_pk PRIMARY KEY (cityname, countryname)" +
                    ");";
            connection.createQuery(sqlCreateTable).executeUpdate();

            String aliasCityName = "COMMENT ON COLUMN " + escapeTableName("city") + ".cityname IS '{{ManyWhoName:City Name}}';";
            String aliasCountryName = "COMMENT ON COLUMN " + escapeTableName("city") + ".countryname IS '{{ManyWhoName:Country Name}}';";
            connection.createQuery(aliasCityName).executeUpdate();
            connection.createQuery(aliasCountryName).executeUpdate();

            String sqlInsert = "INSERT INTO " + escapeTableName("city")+ "(cityname, countryname) VALUES ('Montevideo', 'Uruguay');";
            connection.createQuery(sqlInsert).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data/delete",
                "suites/common/data/delete-with-alias/request.json",
                configurationParameters(),
                "suites/common/data/delete-with-alias/response.json",
                dispatcher
        );

        try (Connection connection = getSql2o().open()) {
            String sql = "SELECT count(cityname) From " + escapeTableName("city")+ " WHERE cityname = 'Montevideo' and countryname ='Uruguay';";
            int found = connection.createQuery(sql).executeScalar(Integer.class);
            assertEquals(0, found);
        }
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
