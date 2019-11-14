package com.manywho.services.sql.suites.mysql.data;

import com.manywho.services.sql.DbConfigurationTest;
import com.manywho.services.sql.ServiceFunctionalTest;
import com.manywho.services.sql.utilities.DefaultApiRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;

public class CapitalLetterTest extends ServiceFunctionalTest {

    @Before
    public void setupDatabase() throws Exception {
        DbConfigurationTest.setPropertiesIfNotInitialized("mysql");
        try (Connection connection = getSql2o().open()) {
            String sql = "CREATE TABLE " + escapeTableName("country") + "("+
                            "`Id` integer NOT NULL,"+
                            "`Name` character varying(255)," +
                            "`DescriptionCountry` character varying(1024)," +
                            "CONSTRAINT country_id_capitalize_pk PRIMARY KEY (`Id`)" +
                        ");";

            connection.createQuery(sql).executeUpdate();
        }
    }


    @Test
    public void testLoadDataByEqualOrLikeFilterCapitalLetters() throws Exception {

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "(`Id`, `Name`, `DescriptionCountry`) VALUES " +
                    "('1', 'Uruguay', 'It is a nice country')," +
                    "('2', 'England', 'It is a beautiful country');";

            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.loadDataRequestAndAssertion("/data",
                "suites/mysql/capitalize/equal-or-like/load-request.json",
                configurationParameters(),
                "suites/mysql/capitalize/equal-or-like/load-response.json",
                dispatcher
        );
    }

    @Test
    public void testCreateData() throws Exception {

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/mysql/capitalize/save/create/create-request.json",
                configurationParameters(),
                "suites/mysql/capitalize/save/create/create-response.json",
                dispatcher
        );
    }

    @Test
    public void testUpdateData() throws Exception {

        try (Connection connection = getSql2o().open()) {
            String sql = "INSERT INTO " + escapeTableName("country") + "(`Id`, `Name`, `DescriptionCountry`) VALUES ('1', 'Uruguay', 'It is a nice country');";
            connection.createQuery(sql).executeUpdate();
        }

        DefaultApiRequest.saveDataRequestAndAssertion("/data",
                "suites/mysql/capitalize/save/update/update-request.json",
                configurationParameters(),
                "suites/mysql/capitalize/save/update/update-response.json",
                dispatcher
        );
    }



    @After
    public void cleanDatabaseAfterEachTest() {
        try (Connection connection = getSql2o().open()) {
            deleteTableIfExist("country", connection);
        } catch (ClassNotFoundException e) {
        }
    }
}
