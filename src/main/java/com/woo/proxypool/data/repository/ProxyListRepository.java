package com.woo.proxypool.data.repository;

import com.woo.proxypool.data.entity.ProxyList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProxyListRepository extends JpaRepository<ProxyList, Long> {
}