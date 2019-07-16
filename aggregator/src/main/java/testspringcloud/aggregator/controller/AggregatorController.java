package testspringcloud.aggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.aggregator.service.PeopleService;

@RestController
public class AggregatorController {

  private final PeopleService peopleService;

  @Autowired
  public AggregatorController(PeopleService peopleService) {
    this.peopleService = peopleService;
  }

  @GetMapping("agg/peoples/age/{id}")
  public String getAge(@PathVariable("id") Integer id) {
    return "{ \"age\": " + peopleService.getById(id).getAge() + " }";
  }
}
