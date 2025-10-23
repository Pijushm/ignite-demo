package com.example.ignite_demo;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/loadAll")
  public String loadAllUsersIntoCache() throws InterruptedException {
    return userService.loadAllUsersIntoCache();
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable("id") int id) {
    return userService.getUser(id);
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.createUser(user);
  }
//
//  @PutMapping("/update")
//  public User updateUser(@RequestBody User user) {
//    return userService.updateUser(user);
//  }
//
//  @DeleteMapping("/{id}")
//  public boolean deleteUser(@PathVariable("id") int id) {
//    return userService.deleteUser(id);
//  }
}