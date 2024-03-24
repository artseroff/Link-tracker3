package edu.java.scrapper.domain.jooq;

import edu.java.scrapper.configuration.db.AccessType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface JooqTest {
    @DynamicPropertySource
    static void setAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> AccessType.JOOQ);
    }
}
