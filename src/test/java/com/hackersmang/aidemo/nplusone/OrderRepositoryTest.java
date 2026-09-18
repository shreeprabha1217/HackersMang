package com.hackersmang.aidemo.nplusone;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is the test that ships in every PR and stays green — it asserts on
 * the RESULT, and the result is correct either way. Run it on its own
 * during the talk to show the query count, which is the number that
 * actually tells the story:
 *
 *   mvn -Dtest=OrderRepositoryTest test
 */
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void buggyVersionReturnsTheRightNumberInTooManyQueries() {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        double total = orderService.totalRevenueBuggy();

        long queries = stats.getQueryExecutionCount();
        System.out.println("Revenue: " + total);
        System.out.println("Queries executed: " + queries);

        assertEquals(200 * (19.99 + 49.50 + 9.75), total, 0.01); // PASSES — the number is correct
        // 1 for findAll() + 1 per order for the lazy items collection = 201.
        // Left as an assertion (rather than just a printout) so you can flip
        // it live: this line fails today, and passes once OrderService is
        // switched over to totalRevenueFixed() / findAllWithItems().
        assertEquals(1, queries, "Expected 1 query, ran " + queries + " — that's the N+1 bug");
    }

    @Test
    void fixedVersionUsesOneQuery() {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        double total = orderService.totalRevenueFixed();

        long queries = stats.getQueryExecutionCount();
        System.out.println("Revenue: " + total);
        System.out.println("Queries executed: " + queries);

        assertEquals(200 * (19.99 + 49.50 + 9.75), total, 0.01);
        assertEquals(1, queries); // PASSES — JOIN FETCH collapses it to one round trip
    }
}
