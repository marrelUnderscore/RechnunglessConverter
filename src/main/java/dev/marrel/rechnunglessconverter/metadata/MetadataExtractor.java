package dev.marrel.rechnunglessconverter.metadata;

import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;

@FunctionalInterface
public interface MetadataExtractor {
    String getValue(ZUGFeRDImporter xrechung);
}
