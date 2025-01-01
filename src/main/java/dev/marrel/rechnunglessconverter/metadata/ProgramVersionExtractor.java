package dev.marrel.rechnunglessconverter.metadata;

import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;

public class ProgramVersionExtractor implements MetadataExtractor {
    @Override
    public String getValue(ZUGFeRDImporter xrechung) {
        return "RechnunglessConverter v" + ProgramVersionExtractor.class.getPackage().getImplementationVersion()/* + " w/ MustangProject v" + ZUGFeRDImporter.class.getPackage().getImplementationVersion()*/;
    }
}
