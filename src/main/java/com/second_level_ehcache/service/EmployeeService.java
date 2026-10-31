
package com.second_level_ehcache.service;

import com.second_level_ehcache.entity.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class EmployeeService {

    public static void main(String[] args) {

        // Second-Level Cache is optional in Hibernate.
        // We need to configure a cache provider like Ehcache
        // and enable Second-Level Cache in persistence.xml.

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("jpa-example");

        /* EntityManager open here */

        EntityManager entityManager = emf.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // Here creating employee record/object

        Employee emp = new Employee();
        emp.setName("Rupak Shubhakant Kar");
        emp.setSalary(168908.0);

        // Here our employee object is initially in the
        // First-Level Cache (Persistence Context) of EntityManager.
        // It is NOT directly stored in Second-Level Cache.

        entityManager.persist(emp);

        /*
         * Hibernate makes the entity managed and adds it to the
         * Persistence Context (First-Level Cache).
         *
         * Hibernate schedules the INSERT operation.
         */

        transaction.commit();


        /*
         * During flush/commit, Hibernate executes the INSERT SQL.
         * The employee record is stored in the database.
         *
         * Important:
         * Second-Level Cache is not populated just because we call persist().
         * It is populated through Hibernate's cache mechanism when the
         * entity is loaded or otherwise put into the cache.
         */

        // ============================================================


        EntityManager entityManager1 = emf.createEntityManager();

        EntityTransaction entityTransaction1 =
                entityManager1.getTransaction();

        entityTransaction1.begin();

        Employee emp1 = entityManager1.find(Employee.class, 1);

        /*
         * First request:
         *
         * EntityManager 1 checks its First-Level Cache.
         * Employee is not available there.
         *
         * Hibernate checks Second-Level Cache.
         *
         * If the employee is not available in Second-Level Cache,
         * Hibernate executes SELECT SQL in the database.
         *
         * The fetched employee is stored in:
         * 1. EntityManager 1's First-Level Cache.
         * 2. Second-Level Cache (if caching is enabled for Employee).
         */

        System.out.println(
                "EntityManager-1 Employee coming from DB: " + emp1);

        System.out.println("Closing EntityManager-1..............");

        entityTransaction1.commit();
        entityManager1.close();

        /*
         * When EntityManager 1 closes:
         *
         * Its First-Level Cache is cleared.
         * Employee becomes detached.
         *
         * But Second-Level Cache still contains the employee.
         */

        // ============================================================

        EntityManager entityManager2 = emf.createEntityManager();

        EntityTransaction transaction2 = entityManager2.getTransaction();
        transaction2.begin();

        Employee emp2 = entityManager2.find(Employee.class, 1);

        /*
         * Second request:
         *
         * EntityManager 2 checks its First-Level Cache.
         * Employee is not available there because this is a new
         * EntityManager.
         *
         * Hibernate checks Second-Level Cache.
         *
         * Employee is already available in Second-Level Cache.
         *
         * Therefore, Hibernate gets the employee from Second-Level Cache.
         * No SELECT SQL is executed against the database,
         * assuming the cache entry is valid and usable.
         */

        System.out.println(
                "EntityManager-2 Employee coming from L2 Cache: " + emp2);

        transaction2.commit();
        entityManager2.close();

        // ============================================================

        /*
         * If we want to clear Second-Level Cache manually:
         */

        emf.getCache().evict(Employee.class);

        // Removes all Employee records from Second-Level Cache.

        emf.close();

        /*
         * What is the scope of Second-Level Cache?
         *
         * Second-Level Cache is maintained at the SessionFactory /
         * EntityManagerFactory level.
         *
         * - EntityManager 1 → Has its own First-Level Cache.
         * - EntityManager 2 → Has its own First-Level Cache.
         * - Both EntityManagers share the same Second-Level Cache,
         *   provided they use the same EntityManagerFactory.
         *
         * Unlike First-Level Cache, Second-Level Cache is shared
         * between multiple Sessions / EntityManagers.
         */

        // ============================================================

        /*
         * What happens when we close the EntityManager?
         *
         * When the EntityManager is closed:
         *
         * - Its First-Level Cache is cleared.
         * - Managed entities become detached.
         * - Its Persistence Context ends.
         *
         * IMPORTANT:
         * Second-Level Cache is NOT cleared when an EntityManager closes.
         * It remains available to other EntityManagers using the same
         * EntityManagerFactory.
         */

        // ============================================================

        /*
         * Can we clear Second-Level Cache manually?
         *
         * Yes. Hibernate provides the following methods:
         *
         * Method                         Purpose
         *
         * entityManager.clear()
         * Removes all entities from the current First-Level Cache.
         *
         * entityManager.detach(entity)
         * Removes a particular entity from the current First-Level Cache.
         *
         * emf.getCache().evict(Employee.class)
         * Removes all Employee entities from the JPA Second-Level Cache.
         *
         * emf.getCache().evictAll()
         * Removes all entities from the JPA Second-Level Cache.
         *
         * Important:
         * entityManager.clear() does NOT clear Second-Level Cache.
         */

        // ============================================================


    }
}
