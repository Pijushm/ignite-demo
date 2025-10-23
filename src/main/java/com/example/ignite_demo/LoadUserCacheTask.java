package com.example.ignite_demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;

public class LoadUserCacheTask implements IgniteRunnable {

  @IgniteInstanceResource
  private Ignite ignite;

  private static final double BYTES_PER_MB = 1024.0 * 1024.0;

//  @Override
//  public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, Object arg) {
//    // Distribute to ALL nodes for consistency
//    Map<ComputeJob, ClusterNode> jobs = new HashMap<>();
//    for (ClusterNode node : subgrid) {
//      jobs.put(new LoadCacheJob(), node);
//    }
//    return jobs;
//  }
//
//  @Override
//  public String reduce(List<ComputeJobResult> results) {
//    StringBuilder sb = new StringBuilder("Cache load results:\n");
//    for (ComputeJobResult res : results) {
//      if (res.getException() != null) {
//        sb.append("ERROR on ").append(res.getNode().id())
//            .append(": ").append(res.getException().getMessage()).append("\n");
//      } else {
//        sb.append((String) res.getData()).append("\n");
//      }
//    }
//    return sb.toString();
//  }

  @Override
  public void run() {
    IgniteCache<Long, User> cache = ignite.cache("userCache");
    cache.loadCache(null);
  }


}