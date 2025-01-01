package dev.marrel.rechnunglessconverter;

import dev.marrel.rechnunglessconverter.metadata.MetadataPoint;
import org.mustangproject.ZUGFeRD.ZUGFeRDExportException;
import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;
import org.mustangproject.ZUGFeRD.ZUGFeRDVisualizer;
import org.mustangproject.validator.ZUGFeRDValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;


public class RechnunglessService {

    public static final boolean PARSE_INVALID_XMLS = "TRUE".equalsIgnoreCase(System.getenv("RECHUNGLESS_PARSEINVALIDXMLS"));


    /**
     * Extract metadata from the given XRechnung
     *
     * @param xRechnung The XML of a XRechnung
     * @return The metadata extracted from the XRechnung
     */
    public HashMap<String, String> getMetadata(String xRechnung) {
        try {
            ZUGFeRDImporter importer = new ZUGFeRDImporter();
            if (PARSE_INVALID_XMLS) {
                importer.doIgnoreCalculationErrors();
            }
            importer.fromXML(xRechnung);

            HashMap<String, String> metadataMap = new HashMap<>();
            for (MetadataPoint datapoint : MetadataPoint.values()) {
                try {
                    if (datapoint.getValue(importer) != null && !datapoint.getValue(importer).isBlank()) {
                        metadataMap.put(datapoint.name(), datapoint.getValue(importer));
                    }
                } catch (NullPointerException ignored) {
                }
            }
            return metadataMap;

        } catch (ArithmeticException | ZUGFeRDExportException e) {
            throw new InvalidXRechnungException("Invalid XRechnung: " + e.getMessage());
        }
    }


    /**
     * Generate a ZUGFeRD PDF from a XRechnung
     *
     * @param xRechnung
     * @return
     * @throws IOException
     */
    public String generateZugferdPdf(String xRechnung) throws IOException {

        // Currently there is no exposed API for direct conversion mustangproject, so we use this
        // workaround with temporary files. If we get an improved API, this code should be improved.

        java.nio.file.Path tempXmlFile = Files.createTempFile("invoice-", ".xml");
        java.nio.file.Path tempPdfFile = Files.createTempFile("invoice-", ".pdf");

        Files.writeString(tempXmlFile, xRechnung,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        ZUGFeRDVisualizer zvi = new ZUGFeRDVisualizer();
        zvi.toPDF(tempXmlFile.toAbsolutePath().toString(), tempPdfFile.toAbsolutePath().toString());

        byte[] pdfFile = Files.readAllBytes(tempPdfFile);

        Files.delete(tempXmlFile);
        Files.delete(tempPdfFile);

        return Base64.getEncoder().encodeToString(pdfFile);
    }


    /**
     * Validate the XRechnung XML
     *
     * @param xRechnungXML The XRechnung to be validated
     * @return A validation result
     */
    protected ValidationResult validateXRechnung(String xRechnungXML) {
        ZUGFeRDValidator zva = new ZUGFeRDValidator();
        String mustangValidationResult = zva.validate(xRechnungXML.getBytes(StandardCharsets.UTF_8), "invoice-stream.xml");

        ValidationResult validationResult = new ValidationResult(mustangValidationResult);

        if (!PARSE_INVALID_XMLS && !validationResult.isValid()) {
            // XML is invalid, skip visualization
            throw new InvalidXRechnungException(validationResult);
        }

        return validationResult;
    }
}
