package com.second_level_ehcache.entity;


import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;


@Data
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "employeeCache")//here multiple thread can perform at the same time
// so i kept concurrency READ_WRITE
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double salary;
}
