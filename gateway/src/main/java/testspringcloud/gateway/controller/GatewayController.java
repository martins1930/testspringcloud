package testspringcloud.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import testspringcloud.gateway.service.PeopleService;

@RestController
public class GatewayController {

  private final PeopleService peopleService;

  @Autowired
  public GatewayController(PeopleService peopleService) {
    this.peopleService = peopleService;
  }

  @GetMapping("gw/peoples/age/{id}")
  public String getAge(@PathVariable("id") Integer id) {
    return "{ \"age\": " + peopleService.getById(id).getAge() + " }";
  }
}
