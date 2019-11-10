package com.manywho.services.sql;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.services.jaxrs.resolvers.ObjectMapperContextResolver;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.junit.BeforeClass;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.PostgresQuirks;

import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Objects;

public abstract class ServiceFunctionalTest {
    private Sql2o sql2o;
    protected static Dispatcher dispatcher;
    protected static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUp() {
        TestApplication application = new TestApplication();

        objectMapper = new ObjectMapperContextResolver().getContext(null);
        dispatcher = MockDispatcherFactory.createDispatcher();

        for (Class<?> klass : application.getClasses()) {
            dispatcher.getRegistry().addPerRequestResource(klass);
        }

        dispatcher.getProviderFactory().registerProvider(ObjectMapperContextResolver.class);

        for (Object singleton : application.getSingletons()) {
            if (singleton.getClass().isAnnotationPresent(Path.class)) {
                dispatcher.getRegistry().addSingletonResource(singleton);
            } else if (singleton.getClass().getSuperclass().isAnnotationPresent(Path.class)) {
                dispatcher.getRegistry().addSingletonResource(singleton);
            }
        }
    }

    protected Sql2o getSql2o() throws ClassNotFoundException {
        if(sql2o != null) {

            return sql2o;
        } else {
            if(Objects.equals(DbConfigurationTest.databaseTypeForTest, "postgresql")) {
                Class.forName("org.postgresql.Driver");
                sql2o = new Sql2o("jdbc:postgresql://" + DbConfigurationTest.hostForTest+":" + DbConfigurationTest.portForTest + "/" + DbConfigurationTest.databaseNameForTest, DbConfigurationTest.userName, DbConfigurationTest.password,
                        new PostgresQuirks());

            }else if(Objects.equals(DbConfigurationTest.databaseTypeForTest, "sqlserver")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                sql2o = new Sql2o("jdbc:sqlserver://" + DbConfigurationTest.hostForTest + ":" + DbConfigurationTest.portForTest + ";databaseName=" + DbConfigurationTest.databaseNameForTest, DbConfigurationTest.userName, DbConfigurationTest.password,
                        new NoQuirks());

            }else if(Objects.equals(DbConfigurationTest.databaseTypeForTest, "mysql")) {
                Class.forName("org.mariadb.jdbc.Driver");
                sql2o = new Sql2o("jdbc:mysql://" + DbConfigurationTest.hostForTest + ":" + DbConfigurationTest.portForTest + "/" + DbConfigurationTest.databaseNameForTest, DbConfigurationTest.userName, DbConfigurationTest.password,
                        new NoQuirks());
            }

            return sql2o;
        }
    }

    protected HashMap<String, String> configurationParameters() {
        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{{port}}", DbConfigurationTest.portForTest);
        replacements.put("{{databaseType}}", DbConfigurationTest.databaseTypeForTest);
        replacements.put("{{schema}}", DbConfigurationTest.schemaForTest);
        replacements.put("{{host}}", DbConfigurationTest.hostForTest);
        replacements.put("{{userName}}", DbConfigurationTest.userName);
        replacements.put("{{password}}", DbConfigurationTest.password);
        replacements.put("{{databaseName}}", DbConfigurationTest.databaseNameForTest);

        return  replacements;
    }

    protected void deleteTableIfExist(String tableName, Connection connection){
        try {
            String sql;
            switch (DbConfigurationTest.databaseTypeForTest) {
                case "sqlserver":
                    sql = "DROP TABLE "+ escapeTableName(tableName);
                    break;
                default:
                    sql = "DROP TABLE IF EXISTS "+ escapeTableName(tableName);
            }

            connection.createQuery(sql).executeUpdate();
        } catch (Exception ex){
        }
    }

    public String escapeTableName(String tableName) {
        String format = "%s.\"%s\"";

        if(Objects.equals(DbConfigurationTest.databaseTypeForTest, "mysql")){
            format = "`%s`.`%s`";
        }

        return String.format(format, DbConfigurationTest.schemaForTest, tableName);
    }
}