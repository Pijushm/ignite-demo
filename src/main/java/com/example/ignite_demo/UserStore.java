package com.example.ignite_demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.springframework.stereotype.Component;

@Component
public class UserStore extends CacheStoreAdapter<Long, User> {

  @CacheStoreSessionResource
  private CacheStoreSession ses;

  public static final int MIN_MEMORY = 1024 * 1024 * 1024;

  /** {@inheritDoc} */
  @Override
  public User load(Long key) {
    System.out.println(">>> Store load [key=" + key + ']');

    try (Connection conn = connection()) {
      try (PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
        st.setLong(1, key);

        ResultSet rs = st.executeQuery();
        return rs.next() ? new User(rs.getLong("id"), rs.getString("name"), rs.getString("email")) : null;
      } catch (SQLException e) {
        throw new CacheLoaderException("Failed to load object [key=" + key + ']', e);
      }
    } catch (SQLException e) {
      throw new CacheLoaderException("Failed to load values from cache store.", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void write(Cache.Entry<? extends Long, ? extends User> entry) {
    Long key = entry.getKey();
    User val = entry.getValue();

    System.out.println(">>> Store write [key=" + key + ", val=" + val + ']');

    try (Connection conn = connection()) {
      int updated;
      try (PreparedStatement st = conn.prepareStatement(
          "UPDATE users SET name = ?, email = ? WHERE id = ?")) {
        st.setString(1, val.getName());
        st.setString(2, val.getEmail());
        st.setLong(3, val.getId());

        updated = st.executeUpdate();
      }

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

    try (Connection conn = connection()) {
      try (PreparedStatement st = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
        st.setLong(1, (Long) key);
        st.executeUpdate();
      } catch (SQLException e) {
        throw new CacheWriterException("Failed to delete object [key=" + key + ']', e);
      }
    } catch (SQLException e) {
      throw new CacheLoaderException("Failed to delete object.", e);
    }
  }


  @Override
  public void loadCache(IgniteBiInClosure<Long, User> clo, Object... args) {
    Connection conn = null;
    boolean closeConn = true;


    try {
      conn = connection();

      try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users")) {
        ResultSet rs = stmt.executeQuery();

        int cnt = 0;
        while (rs.next()) {
          User user = new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
          clo.apply(user.getId(), user);
          cnt++;
        }

        System.out.println(">>> Loaded " + cnt + " values into cache.");
      }
    } catch (SQLException e) {
      throw new CacheLoaderException("Failed to load values from cache store.", e);
    } finally {
      if (conn != null && closeConn) {
        try {
          conn.close();
        } catch (SQLException ignored) {
        }
      }
    }
  }

  private Connection connection() throws SQLException {
    if (ses != null && ses.isWithinTransaction()) {
      Connection conn = ses.attachment();

      if (conn == null) {
        conn = openConnection(false);

        // Store connection in the session, so it can be accessed
        // for other operations within the same transaction.
        ses.attach(conn);
      }

      return conn;
    }
    // Transaction can be null in case of simple load or put operation.
    else
      return openConnection(true);
  }

  private Connection openConnection(boolean autocommit) throws SQLException {
    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ignite_db", "test", "test");
    conn.setAutoCommit(autocommit);

    return conn;
  }

//  public static void checkMinMemory(long min) {
//    long maxMem = Runtime.getRuntime().maxMemory();
//
//    if (maxMem < .85 * min) {
//      System.err.println("Heap limit is too low (" + (maxMem / (1024 * 1024)) +
//          "MB), please increase heap size at least up to " + (min / (1024 * 1024)) + "MB.");
//
//      System.exit(-1);
//    }
//  }
}