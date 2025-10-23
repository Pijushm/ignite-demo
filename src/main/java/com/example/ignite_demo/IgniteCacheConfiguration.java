package com.example.ignite_demo;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteCacheConfiguration {

  @Bean
  public IgniteClient igniteClient() {
    try {
      ClientConfiguration cfg = new ClientConfiguration()
          .setAddresses(
              "127.0.0.1:10800"
          );
      System.out.println("Think client started");
      return Ignition.startClient(cfg);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start Ignite client", e);
    }
  }
}