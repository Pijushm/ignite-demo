// file: `src/main/java/com/example/ignite_demo/IgniteCacheConfiguration.java`
package com.example.ignite_demo;

import javax.cache.configuration.FactoryBuilder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteCacheConfiguration {

  @Bean("igniteConfiguration")
  public IgniteConfiguration igniteConfiguration(UserRepository userRepository) {
    IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
    igniteConfiguration.setIgniteInstanceName("test-ignite");
    igniteConfiguration.setClientMode(false);

    CacheConfiguration<Long, User> cacheConfiguration = new CacheConfiguration<>();
    cacheConfiguration.setName("userCache");
    cacheConfiguration.setBackups(1);
    cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
    cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);

    cacheConfiguration.setReadThrough(true);
    cacheConfiguration.setWriteThrough(true);

    cacheConfiguration.setCacheStoreFactory(FactoryBuilder.factoryOf(
        UserStore.class));

    igniteConfiguration.setCacheConfiguration(cacheConfiguration);
    return igniteConfiguration;
  }

  @Bean(destroyMethod = "close")
  Ignite ignite(IgniteConfiguration igniteConfiguration) throws IgniteException {
    return Ignition.getOrStart(igniteConfiguration);
  }

  @Bean("userIgniteCache")
  public IgniteCache<Long, User> userIgniteCache(Ignite ignite) {
    return ignite.getOrCreateCache("userCache");
  }
}