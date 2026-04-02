package nl.haarlem.translations.zdstozgw.requesthandler;

import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Junie
 * 2026-04-02
 * Test to verify ID generation for RequestResponseCycle.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class RequestResponseCycleIdTest {

    @Autowired
    private RequestResponseCycleRepository repository;

    @Test
    public void testIdGeneration() {
        RequestResponseCycle cycle1 = new RequestResponseCycle();
        cycle1.setModus("test1");
        cycle1 = repository.save(cycle1);
        assertNotNull(cycle1.getId());
        long firstId = cycle1.getId();

        RequestResponseCycle cycle2 = new RequestResponseCycle();
        cycle2.setModus("test2");
        cycle2 = repository.save(cycle2);
        assertNotNull(cycle2.getId());

        assertNotEquals(cycle1.getId(), cycle2.getId());

        RequestResponseCycle cycleWithManualId = new RequestResponseCycle();
        cycleWithManualId.setId(firstId + 10);
        cycleWithManualId.setModus("manual");
        repository.save(cycleWithManualId);

        // This might not fail if the sequence is at 2, it will just pick 3.
        // But if the sequence was at 157 and we manual 158, next sequence will be 158 -> boom.
        // Hibernate's SEQUENCE generator with allocationSize=1 calls nextval for EVERY insert.
    }
}
