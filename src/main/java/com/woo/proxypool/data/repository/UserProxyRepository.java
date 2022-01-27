package com.woo.proxypool.data.repository;

import com.woo.proxypool.data.entity.UserProxyMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProxyRepository extends JpaRepository<UserProxyMap, Long> {
    UserProxyMap findFirstByUserId(String userId);

    @Query("select u from UserProxyMap u where u.proxy = ?1")
    List<UserProxyMap> findByProxy(String proxy);
}
