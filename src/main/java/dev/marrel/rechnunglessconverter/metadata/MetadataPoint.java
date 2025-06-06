package dev.marrel.rechnunglessconverter.metadata;

import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;

public enum MetadataPoint implements MetadataExtractor {
    issueDate ("invoice", ZUGFeRDImporter::getIssueDate),
    dueDate ("invoice", ZUGFeRDImporter::getDueDate),
    totalAmount ("invoice", ZUGFeRDImporter::getAmount),
    taxAmount ("invoice", ZUGFeRDImporter::getTaxTotalAmount),
    preTaxAmount ("invoice", ZUGFeRDImporter::getTaxBasisTotalAmount),
    buyerName ("invoice", ZUGFeRDImporter::getBuyerTradePartyName),
    paymentTerms ("invoice", ZUGFeRDImporter::getPaymentTerms),
    invoiceID ("invoice", ZUGFeRDImporter::getInvoiceID),
    sellerBIC ("invoice", ZUGFeRDImporter::getBIC),
    sellerBankName ("invoice", ZUGFeRDImporter::getBankName),
    sellerIBAN ("invoice", ZUGFeRDImporter::getIBAN),
    currency ("invoice", ZUGFeRDImporter::getInvoiceCurrencyCode),
    deliveryPeriod ("invoice", new DeliveryPeriodExtractor()),
    programVersion("rechnungless", new ProgramVersionExtractor()),
    validity("invoice", null),
    validityReason("invoice", null)
    ;

    private final MetadataExtractor extractor;
    public final String prefix;
    public final boolean autoProcess;
    MetadataPoint(MetadataExtractor extractor) {
        this("", extractor);
    }
    MetadataPoint(String prefix, MetadataExtractor extractor) {
        this.extractor = extractor;
        this.prefix = prefix;
        this.autoProcess = extractor != null;
    }

    @Override
    public String getValue(ZUGFeRDImporter invoice) {
        return this.extractor.getValue(invoice);
    }
}
