package dev.marrel.rechnunglessconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class RechnunglessResourceTest {

    private static final Path RESOURCE_PATH_WITH_INVOICES = Path.of("src", "test", "resources");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final RechnunglessResource RECHNUNGLESS_RESOURCE = new RechnunglessResource();


    /**
     * This method searches for all files ending with .xml in the resources folder.
     * A folder in the path to the XML file should end with ".valid" or ".invalid",
     * otherwise the file is skipped, as the expectation is unknown.
     */
    private static Stream<Arguments> searchInvoiceFiles() {
        try (final Stream<Path> paths = Files.walk(RESOURCE_PATH_WITH_INVOICES)
                .filter(file -> Files.isRegularFile(file) && file.getFileName().toString().toLowerCase().endsWith(".xml"))) {

            Map<Path, Boolean> pathMap = new HashMap<>();
            paths.forEach(path -> {
                Boolean isValid = null;
                for (int i = 0; isValid == null && i < path.getNameCount(); i++) {
                    String folderName = path.getName(i).toString();
                    if (folderName.endsWith(".valid")) {
                        isValid = true;
                    }
                    if (folderName.endsWith(".invalid")) {
                        isValid = false;
                    }
                }
                if (isValid != null) {
                    pathMap.put(path, isValid);
                } else {
                    System.out.println("Skip " + path + " due to unknown expectation");
                }
            });

            System.out.println("Found " + pathMap.size() + " XML files in " + RESOURCE_PATH_WITH_INVOICES.toAbsolutePath());

            return pathMap.entrySet().stream().map(entry -> Arguments.of(entry.getKey(), entry.getValue()));

        } catch (IOException e) {
            fail(e.getMessage());
            return null;
        }
    }


    /**
     * Converts XRechnung files and checks expectations
     *
     * @param xRechnungPath Path of the XRechnung file
     * @param isValid       Expectation if file is valid or not
     */
    @ParameterizedTest
    @MethodSource("searchInvoiceFiles")
    void convert(Path xRechnungPath, boolean isValid) throws IOException {
        System.out.println(xRechnungPath);
        String xRechung = Files.readString(xRechnungPath);
        try (Response result = RECHNUNGLESS_RESOURCE.convert(xRechung)) {
            ResponseDto body = (ResponseDto) result.getEntity();
            System.out.println(OBJECT_MAPPER.writeValueAsString(body.getMessages()));
            if (isValid) {
                assertEquals(200, result.getStatus(), "Expected a valid XRechung in '" + xRechnungPath.toAbsolutePath() + "'. Response body was: " + OBJECT_MAPPER.writeValueAsString(body));
                assertEquals("success", body.getResult());
                assertNotNull(body.getArchivePdf());
            } else {
                assertEquals(422, result.getStatus());
                assertNull(body.getArchivePdf());
            }
        }
    }
}
