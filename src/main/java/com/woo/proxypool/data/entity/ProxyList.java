package com.woo.proxypool.data.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "proxy_list")
public class ProxyList {
    @Id
    @GeneratedValue
    Long id;
    String ip;
    Integer status;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;
}
