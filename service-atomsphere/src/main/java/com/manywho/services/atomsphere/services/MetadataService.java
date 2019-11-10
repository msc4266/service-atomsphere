package com.manywho.services.atomsphere.services;

import com.manywho.services.atomsphere.entities.TableMetadata;
import com.manywho.services.atomsphere.exceptions.DataBaseTypeNotSupported;
import com.manywho.services.atomsphere.utilities.ContentTypeUtil;

import javax.inject.Inject;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetadataService {

    @Inject
    public MetadataService(){
    }

    public List<TableMetadata> getTablesMetadata(String databaseName, String schemaName, DatabaseMetaData metaData) throws Exception {
        String[] types = {"TABLE", "VIEW"};

        return this.getTablesMetadataInternal(databaseName, schemaName, null, types, metaData);
    }

    public List<TableMetadata> getTablesMetadata(String databaseName, String schemaName, DatabaseMetaData metaData, String tableName) throws Exception {
        String[] types = {"TABLE", "VIEW"};

        return this.getTablesMetadataInternal(databaseName, schemaName, tableName, types, metaData);
    }

    private List<TableMetadata> getTablesMetadataInternal(String catalog, String schemaName, String tableNamePattern, String[] types, DatabaseMetaData metaData) throws Exception {
        ResultSet rsTablesMetadata = metaData.getTables(catalog, schemaName, tableNamePattern, types );
        List<TableMetadata> tableList = new ArrayList<>();

        while(rsTablesMetadata.next()) {
            boolean isView = "VIEW".equals(rsTablesMetadata.getString(4));

            TableMetadata tableMetadata = new TableMetadata(isView, rsTablesMetadata.getString(3), rsTablesMetadata.getString(5), schemaName);

            populateColumnForTable(catalog, schemaName, tableMetadata, metaData);
            populatePrimaryKeyForTable(catalog, schemaName, tableMetadata, metaData);
            tableList.add(tableMetadata);
        }

        return tableList;
    }

    private void populatePrimaryKeyForTable(String databaseName, String databaseSchema, TableMetadata tableMetadata, DatabaseMetaData metaData) throws Exception {
        ResultSet rsPrimaryKey = metaData.getPrimaryKeys(databaseName, databaseSchema, tableMetadata.getTableName());
        ArrayList<String> primaryKeys = new ArrayList<>();

        while (rsPrimaryKey.next()) {
            primaryKeys.add(rsPrimaryKey.getString("COLUMN_NAME"));
        }

        tableMetadata.setPrimaryKeyNames(primaryKeys);
    }

    private void populateColumnForTable(String databaseName, String databaseSchema, TableMetadata tableMetadata, DatabaseMetaData metaData) throws SQLException {
        ResultSet rsColumnsMetadata = metaData.getColumns(databaseName, databaseSchema, tableMetadata.getTableName(), null);

        while (rsColumnsMetadata.next()) {
            try {
                // 6 => "IS_AUTOINCREMENT" but if we use the string is failing in sqlserver
                tableMetadata.setColumn(
                        rsColumnsMetadata.getString("COLUMN_NAME"),
                        ContentTypeUtil.createFromSqlType(rsColumnsMetadata.getInt("DATA_TYPE"), rsColumnsMetadata.getString("TYPE_NAME")),
                        Objects.equals(rsColumnsMetadata.getString("IS_AUTOINCREMENT"), "YES"),
                        rsColumnsMetadata.getString("REMARKS")
                );

                tableMetadata.setColumnsDatabaseType(
                        rsColumnsMetadata.getString(4),
                        addDatabaseSpecificTypes(rsColumnsMetadata.getInt(5),rsColumnsMetadata.getString(6) )
                );

            } catch (DataBaseTypeNotSupported e) {
                // if some type is not supported we just ignore it
            }
        }
    }

    private String addDatabaseSpecificTypes(int type, String databaseSpecificType) {
        switch(type){
            case ContentTypeUtil.SQL_SERVER_DATETIMEOFFSET:
                return ContentTypeUtil.SQL_SERVER_dATETIMEOFFSET_TEXT;
            case ContentTypeUtil.POSTGRESQL_UUID:
                if (Objects.equals(databaseSpecificType, ContentTypeUtil.POSTGRESQL_UUID_TEXT)) {
                    return ContentTypeUtil.POSTGRESQL_UUID_TEXT;
                } else {
                    return JDBCType.valueOf(type).getName();
                }
            default:
                return JDBCType.valueOf(type).getName();
        }
    }
}
