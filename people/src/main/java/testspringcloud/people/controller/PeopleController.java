package testspringcloud.people.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.domain.People;
import testspringcloud.people.config.AppConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    PreparedStatement preparedStatement = connection.prepareStatement("select count(*) as tot from actor");
    ResultSet resultSet = preparedStatement.executeQuery();
    resultSet.next();
    int tot = resultSet.getInt("tot");
    LOGGER.info("Get all people method invoked.");
    return new People(id, "sergio:" + appConfig.getMyValue() + "-tot="+tot, 22);
  }
}
