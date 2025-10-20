package com.example.ignite_demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.springframework.stereotype.Component;

@Component
public class UserStore extends CacheStoreAdapter<Long, User> {

  @CacheStoreSessionResource
  private CacheStoreSession ses;

  /** {@inheritDoc} */
  @Override
  public User load(Long key) {
    System.out.println(">>> Store load [key=" + key + ']');

    Connection conn = ses.attachment();

    try (PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
      st.setLong(1, key);

      ResultSet rs = st.executeQuery();
      return rs.next() ? new User(rs.getLong("id"), rs.getString("name"), rs.getString("email")) : null;
    } catch (SQLException e) {
      throw new CacheLoaderException("Failed to load object [key=" + key + ']', e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void write(Cache.Entry<? extends Long, ? extends User> entry) {
    Long key = entry.getKey();
    User val = entry.getValue();

    System.out.println(">>> Store write [key=" + key + ", val=" + val + ']');

    Connection conn = ses.attachment();
    try {
      int updated;
      // Try update first
      try (PreparedStatement st = conn.prepareStatement(
          "UPDATE users SET name = ?, email = ? WHERE id = ?")) {
        st.setString(1, val.getName());
        st.setString(2, val.getEmail());
        st.setLong(3, val.getId());

        updated = st.executeUpdate();
      }

      // If update failed, try insert
      if (updated == 0) {
        try (PreparedStatement st = conn.prepareStatement(
            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
          st.setLong(1, val.getId());
          st.setString(2, val.getName());
          st.setString(3, val.getEmail());

          st.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new CacheWriterException("Failed to write object [key=" + key + ", val=" + val + ']', e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void delete(Object key) {
    System.out.println(">>> Store delete [key=" + key + ']');

    Connection conn = ses.attachment();

    try (PreparedStatement st = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
      st.setLong(1, (Long) key);
      st.executeUpdate();
    } catch (SQLException e) {
      throw new CacheWriterException("Failed to delete object [key=" + key + ']', e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void loadCache(IgniteBiInClosure<Long, User> clo, Object... args) {
    if (args == null || args.length == 0 || args[0] == null)
      throw new CacheLoaderException("Expected entry count parameter is not provided.");

    final int entryCnt = (Integer) args[0];

    Connection conn = ses.attachment();

    try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users LIMIT ?")) {
      stmt.setInt(1, entryCnt);

      ResultSet rs = stmt.executeQuery();

      int cnt = 0;
      while (rs.next()) {
        User user = new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
        clo.apply(user.getId(), user);
        cnt++;
      }

      System.out.println(">>> Loaded " + cnt + " values into cache.");
    } catch (SQLException e) {
      throw new CacheLoaderException("Failed to load values from cache store.", e);
    }
  }
}
