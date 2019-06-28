package testspringcloud.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import testspringcloud.domain.People;

@Service
public class PeopleService {

  private static final String PEOPLE_URL = "http://people/peoples";

  private final RestTemplate restTemplate;

  @Autowired
  public PeopleService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public People getById(Integer id) {
    return restTemplate.getForObject(PEOPLE_URL + "/" + id, People.class);
  }
}
