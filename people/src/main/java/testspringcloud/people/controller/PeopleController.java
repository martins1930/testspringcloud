package testspringcloud.people.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.domain.People;

@RestController
public class PeopleController {

  @GetMapping("/peoples/{id}")
  public People getAll(@PathVariable("id") Integer id) {
    return new People(id, "sergio", 22);
  }
}
