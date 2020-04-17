package testspringcloud.people.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.Test;

public class JdbcQueryParserTest {

  @Test
  public void shouldParseAndOperator() {
    JdbcQueryParser jdbcQueryParser = new JdbcQueryParser();
    ResourceController.TableMetaData tableMetaData = getTableMetaDataForActor();
    String queryCondition =
        jdbcQueryParser.getWhereCondition("actorId==1;firstName==Martin", tableMetaData);

    assertThat(queryCondition).isEqualTo("(actor_id=1 AND first_name='Martin')");
  }

  private ResourceController.TableMetaData getTableMetaDataForActor() {

    return new ResourceController.TableMetaData(
        Map.of("actor_id", Integer.class, "first_name", String.class),
        Map.of("actor_id", "actorId", "first_name", "firstName"),
        Map.of("actorId", "actor_id", "firstName", "first_name")
    );
  }

  @Test
  public void shouldParseOrOperator() {
    JdbcQueryParser jdbcQueryParser = new JdbcQueryParser();
    ResourceController.TableMetaData tableMetaData = getTableMetaDataForActor();
    String queryCondition =
        jdbcQueryParser.getWhereCondition("actorId==1,firstName==Martin", tableMetaData);

    assertThat(queryCondition).isEqualTo("(actor_id=1 OR first_name='Martin')");
  }

  @Test
  public void shouldParseOrOperatorWithParenthesis() {
    JdbcQueryParser jdbcQueryParser = new JdbcQueryParser();
    ResourceController.TableMetaData tableMetaData = getTableMetaDataForActor();
    String queryCondition = jdbcQueryParser
        .getWhereCondition("(firstName==\"CAGE\",firstName==\"JOHNNY\");actorId==40",
            tableMetaData);

    assertThat(queryCondition)
        .isEqualTo("((first_name='CAGE' OR first_name='JOHNNY') AND actor_id=40)");
  }

}