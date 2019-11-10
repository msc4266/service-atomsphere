package com.manywho.services.atomsphere.managers;

import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.drivers.connection.DriverConfigurationInterface;
import com.manywho.services.atomsphere.drivers.connection.MySqlDriverConfiguration;
import com.manywho.services.atomsphere.drivers.connection.PostgreSqlDriverConfiguration;
import com.manywho.services.atomsphere.drivers.connection.SqlServerDriverConfiguration;
import org.sql2o.Sql2o;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.PostgresQuirks;
import org.sql2o.quirks.Quirks;

public class ConnectionManager {

    public static Sql2o getSql2Object(ServiceConfiguration serviceConfiguration) {

        switch (serviceConfiguration.getDatabaseType()) {
            case Mysql:
                return createSql2o(new MySqlDriverConfiguration(), serviceConfiguration, new NoQuirks());
            case Sqlserver:
                return createSql2o(new SqlServerDriverConfiguration(), serviceConfiguration, new NoQuirks());
            case Postgresql:
                return createSql2o(new PostgreSqlDriverConfiguration(), serviceConfiguration, new PostgresQuirks());
            default:
                throw new RuntimeException(String.format("The database type \"%s\" is not supported",
                        serviceConfiguration.getDatabaseType()));
        }
    }

    private static Sql2o createSql2o(DriverConfigurationInterface driverConfiguration,
                              ServiceConfiguration serviceConfiguration, Quirks quirks) {

        return new Sql2o(
                driverConfiguration.getStringConnection(serviceConfiguration),
                serviceConfiguration.getUsername(),
                serviceConfiguration.getPassword(),
                quirks);
    }
}
