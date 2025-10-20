package com.example.ignite_demo;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStoreAdapter;

public class UserCacheStore extends CacheStoreAdapter<Long, User> {

  private final UserRepository userRepository;

  public UserCacheStore(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User load(Long key) throws CacheLoaderException {
    return userRepository.findById(key).orElse(null);
  }

  @Override
  public void write(Entry<? extends Long, ? extends User> entry) throws CacheWriterException {
    userRepository.save(entry.getValue());
  }

  @Override
  public void delete(Object key) throws CacheWriterException {
    userRepository.deleteById((Long) key);
  }
}
