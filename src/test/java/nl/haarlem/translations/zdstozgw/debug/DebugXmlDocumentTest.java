/*
 * Junie
 * 2026-09-11
 * Unit test to verify nextapp.echo2.webrender.output.XmlDocument renders without NPE
 */
package nl.haarlem.translations.zdstozgw.debug;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import nextapp.echo2.webrender.output.XmlDocument;
import nextapp.echo2.webrender.util.DomUtil;
import org.junit.jupiter.api.Test;

class DebugXmlDocumentTest {

    @Test
    void testDomUtilTransformerFactoryNotNull() {
        assertNotNull(
                DomUtil.getTransformerFactory(),
                "DomUtil TransformerFactory should not be null"
        );
    }

    @Test
    void testXmlDocumentRender() {
        assertDoesNotThrow(() -> {
            XmlDocument xmlDocument = new XmlDocument("test-root", null, null, null);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            xmlDocument.render(printWriter);
            printWriter.flush();
            String output = stringWriter.toString();
            assertNotNull(output);
            assertFalse(output.isEmpty());
        });
    }
}
