package com.woo.proxypool.data.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "proxy_list")
public class ProxyList {
    @Id
    @SequenceGenerator(name= "CLIENT_SEQUENCE", sequenceName = "CLIENT_SEQUENCE_ID", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.AUTO, generator="CLIENT_SEQUENCE")
    Long id;
    String ip;
    Integer status;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    public ProxyList(String ip, Integer status) {
        this.ip = ip;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProxyList{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
