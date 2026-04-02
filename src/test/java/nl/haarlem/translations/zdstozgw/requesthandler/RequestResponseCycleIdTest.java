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
        repository.saveAndFlush(cycle1);
        assertNotNull(cycle1.getId());
        long firstId = cycle1.getId();
        System.out.println("[DEBUG_LOG] cycle1.id: " + firstId);

        RequestResponseCycle cycle2 = new RequestResponseCycle();
        cycle2.setModus("test2");
        repository.saveAndFlush(cycle2);
        assertNotNull(cycle2.getId());
        System.out.println("[DEBUG_LOG] cycle2.id: " + cycle2.getId());

        assertNotEquals(firstId, cycle2.getId());
    }
}
