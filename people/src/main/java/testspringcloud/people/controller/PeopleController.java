package testspringcloud.people.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.domain.People;
import testspringcloud.people.config.AppConfig;

@RestController
public class PeopleController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PeopleController.class);

  private final AppConfig appConfig;
  private final DataSource dataSource;

  public PeopleController(AppConfig appConfig, DataSource dataSource) {
    this.appConfig = appConfig;
    this.dataSource = dataSource;
  }

  @GetMapping("/peoples/{id}")
  public People getAll(@PathVariable("id") Integer id) throws SQLException {
    Connection connection = dataSource.getConnection();
    PreparedStatement preparedStatement = connection.prepareStatement(
        "select table_name, table_type "
            + "from information_schema.tables "
            + "where table_schema = 'public' ");
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      String tableName = resultSet.getString("table_name");
      String tableType = resultSet.getString("table_type");
      LOGGER.info("table_name={}, table_type={}", tableName, tableType);
    }
    LOGGER.info("Get all people method invoked.");
    return new People(id, "sergio:" + appConfig.getMyValue(), 22);
  }
}
