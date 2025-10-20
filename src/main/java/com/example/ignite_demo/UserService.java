package com.example.ignite_demo;


import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private IgniteCache<Long, User> userCache;

  public UserService(IgniteCache<Long, User> userCache) {
    this.userCache = userCache;
    userCache.loadCache(null);
  }

  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    for (javax.cache.Cache.Entry<Long, User> integerPersonEntry : userCache) {
      users.add(integerPersonEntry.getValue());
    }
    return users;
  }

  @Transactional(transactionManager = "transactionManager")
  public User getUser(long id) {
    return (User) userCache.get(id);
  }

  public User createUser(User user  ) {
    return userCache.getAndPut((long) user.getId(), user);
  }

  public User updateUser(User user) {
    return userCache.getAndPut((long) user.getId(), user);
  }

  public boolean deleteUser(long id) {
    if (userCache.containsKey(id)) {
      return userCache.remove(id);
    }
    return false;
  }
}
