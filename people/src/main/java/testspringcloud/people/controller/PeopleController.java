package testspringcloud.people.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.domain.People;

@RestController
public class PeopleController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PeopleController.class);


  @GetMapping("/peoples/{id}")
  public People getAll(@PathVariable("id") Integer id) {
    LOGGER.info("Get all people method invoked.");
    return new People(id, "sergio", 22);
  }
}
