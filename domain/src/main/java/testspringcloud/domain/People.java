package testspringcloud.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Value;

@Data
@Value
public class People {

  private final int id;
  private final String name;
  private final int age;

  @JsonCreator
  public People(
      @JsonProperty("id") int id,
      @JsonProperty("name") String name,
      @JsonProperty("age") int age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }
}
