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
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proxy_list")
public class ProxyList {
    @Id
    @SequenceGenerator(name= "PROXY_SEQUENCE", sequenceName = "PROXY_SEQUENCE_ID", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.AUTO, generator="PROXY_SEQUENCE")
    Long id;
    String ip;
    @Column(name = "usage_count")
    Integer usageCount;
    Integer status;
    @CreationTimestamp
    Date createdAt;
    @UpdateTimestamp
    Date updatedAt;

    public ProxyList(String ip, Integer usageCount, Integer status) {
        this.ip = ip;
        this.usageCount = usageCount;
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
