package testspringcloud.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

public class PeopleTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  public void shouldSerializeAndDeserializeFromJson() throws IOException {
    People people = new People(1, "mytest", 12);

    String peopleJson = MAPPER.writeValueAsString(people);
    People parsedPeople = MAPPER.readValue(peopleJson, People.class);

    assertThat(parsedPeople).isEqualTo(people);
  }
}