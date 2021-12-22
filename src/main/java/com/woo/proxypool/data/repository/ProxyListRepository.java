package com.woo.proxypool.data.repository;

import com.woo.proxypool.data.entity.ProxyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProxyListRepository extends JpaRepository<ProxyList, Long> {

    @Query("select p from ProxyList p where p.status = ?1")
    ProxyList findOneByStatus(Integer status);
}