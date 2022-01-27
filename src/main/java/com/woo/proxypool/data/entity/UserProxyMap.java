package com.woo.proxypool.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "user_proxy_map")
public class UserProxyMap {
    @Id
    @SequenceGenerator(name= "USER_PROXY_MAP_SEQUENCE", sequenceName = "USER_PROXY_MAP_SEQUENCE_ID", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.AUTO, generator="USER_PROXY_MAP_SEQUENCE")
    @Column(name = "id")
    Long id;
    @Column(name = "proxy")
    String proxy;
    @Column(name = "user_id")
    String userId;
    @CreationTimestamp
    @Column(name = "created_at")
    Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    Date updatedAt;

    public UserProxyMap(String proxy, String userId) {
        this.proxy = proxy;
        this.userId = userId;
    }
}
