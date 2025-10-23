package com.example.ignite_demo;


import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final IgniteClient igniteClient;
  private final ClientCache<Long, User> userCache;

  public UserService(IgniteClient igniteClient) {
    this.igniteClient = igniteClient;
    this.userCache = igniteClient.getOrCreateCache("userCache");
  }


  public String loadAllUsersIntoCache() throws InterruptedException {
    return igniteClient.compute().execute(
        "com.example.LoadUserCacheTask",
        null // argument, not needed here
    );
  }

  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, name, email FROM User"; // select explicit fields

    List<List<?>> result = userCache.query(new SqlFieldsQuery(sql)).getAll();

    for (List<?> row : result) {
      Long id = row.get(0) == null ? null : ((Number) row.get(0)).longValue();
      String name = (String) row.get(1);
      String email = (String) row.get(2);
      users.add(new User(id, name, email));
    }

    System.out.println(users.size());
    return users;
  }

  @Transactional(transactionManager = "transactionManager")
  public User getUser(long id) {

    System.out.println(userCache.get(id));
    return userCache.get(id);
  }

  public User createUser(User user) {
    userCache.put(user.getId(), user);
    return user;
  }

  public User updateUser(User user) {
    userCache.put(user.getId(), user);
    return user;
  }

  public boolean deleteUser(long id) {
    return userCache.remove(id);
  }
}
