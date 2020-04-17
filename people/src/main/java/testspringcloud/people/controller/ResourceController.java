package testspringcloud.people.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

  private final DataSource dataSource;
  private final JdbcQueryParser jdbcQueryParser;

  public ResourceController(DataSource dataSource, JdbcQueryParser jdbcQueryParser) {
    this.dataSource = dataSource;
    this.jdbcQueryParser = jdbcQueryParser;
  }

  @GetMapping("/resources/{id}")
  public List<Map<String, Object>> getAll(
      @PathVariable("id") String id, @RequestParam Integer page, @RequestParam Integer size,
      @RequestParam String sort, @RequestParam(defaultValue = "") String search) throws
      SQLException {
    try (Connection connection = dataSource.getConnection()) {
      TableMetaData tableMetaData = getTableMetaData(id, connection);
      String sql = buildSql(tableMetaData, id, page, size, sort, search);
      return executeSqlQuery(connection, tableMetaData, sql);
    }
  }

  private TableMetaData getTableMetaData(String id, Connection connection) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(
        "select column_name, data_type "
            + "from information_schema.columns "
            + "where table_schema = 'public' and table_name = ? ");
    preparedStatement.setString(1, id);
    ResultSet resultSet = preparedStatement.executeQuery();
    boolean emptyResult = true;
    Map<String, Class<?>> columnNameToFieldType = new HashMap<>();
    Map<String, String> columnNameToDocumentName = new HashMap<>();
    Map<String, String> documentNameToColumnName = new HashMap<>();
    while (resultSet.next()) {
      emptyResult = false;
      String columnName = resultSet.getString("column_name");
      Class<?> dataType = getDataType(resultSet.getString("data_type"));
      String columnNameCapitalized = capitalize(columnName);
      columnNameToFieldType.put(columnName, dataType);
      columnNameToDocumentName.put(columnName, columnNameCapitalized);
      documentNameToColumnName.put(columnNameCapitalized, columnName);
    }
    preparedStatement.close();
    resultSet.close();
    if (emptyResult) {
      throw new IllegalArgumentException("Error the resource " + id + "doesn't exists.");
    }
    return new TableMetaData(columnNameToFieldType, columnNameToDocumentName,
        documentNameToColumnName);
  }

  private Class<?> getDataType(String dataType) {
    if (dataType.equalsIgnoreCase("integer")) {
      return Integer.class;
    } else if (dataType.contains("character")) {
      return String.class;
    } else if (dataType.contains("timestamp")) {
      return Timestamp.class;
    }
    return null;
  }

  private List<Map<String, Object>> executeSqlQuery(
      Connection connection, TableMetaData tableMetaData, String sql) throws SQLException {
    LOGGER.info("Executing this SQL query: {}", sql);
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    ResultSet resultSet = preparedStatement.executeQuery();
    List<Map<String, Object>> ret = new LinkedList<>();
    Map<String, String> columnNameToDocumentName = tableMetaData.getColumnNameToDocumentName();
    while (resultSet.next()) {
      Map<String, Object> record = new HashMap<>();
      for (Map.Entry<String, Class<?>> column : tableMetaData.columnNameToFieldType.entrySet()) {
        Class<?> columnType = column.getValue();
        String columnName = column.getKey();
        String documentName = columnNameToDocumentName.get(columnName);
        if (columnType.equals(Integer.class)) {
          int intColumn = resultSet.getInt(columnName);
          if (resultSet.wasNull()) {
            record.put(documentName, null);
          } else {
            record.put(documentName, intColumn);
          }
        } else if (columnType.equals(String.class)) {
          String stringColumn = resultSet.getString(columnName);
          record.put(documentName, stringColumn);
        } else if (columnType.equals(Timestamp.class)) {
          Timestamp timestampColumn = resultSet.getTimestamp(columnName);
          record.put(documentName, timestampColumn);
        }
      }
      ret.add(record);
    }
    preparedStatement.close();
    resultSet.close();
    return ret;
  }

  private String capitalize(String str) {
    String[] splitBySeparator = str.split("_");
    String ret = "";
    for (int i = 0; i < splitBySeparator.length; i++) {
      if (i == 0) {
        ret = splitBySeparator[i];
      } else {
        String current = splitBySeparator[i];
        ret += current.substring(0, 1).toUpperCase() + current.substring(1);
      }
    }
    return ret;
  }

  private String buildSql(
      TableMetaData tableMetaData, String id, Integer page, Integer size, String sort,
      String search) {
    String whereCondition = "";
    if (!search.isEmpty()) {
      whereCondition = " where " + jdbcQueryParser.getWhereCondition(search, tableMetaData);
    }
    return "select " + tableMetaData.getColumnNamesSeparatedByComma()
        + " from " + id
        + whereCondition
        + " order by " + sort
        + " limit " + size + " offset " + page * size;
  }

  @Value
  static class TableMetaData {
    Map<String, Class<?>> columnNameToFieldType;
    Map<String, String> columnNameToDocumentName;
    Map<String, String> documentNameToColumnName;

    private String getColumnNamesSeparatedByComma() {
      return String.join(",", columnNameToDocumentName.keySet());
    }
  }
}
