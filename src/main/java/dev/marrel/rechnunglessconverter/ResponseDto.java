package dev.marrel.rechnunglessconverter;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private String result;

    private List<ValidationMessage> messages;

    private String metadata;

    @JsonProperty("archive_pdf")
    private String archivePdf;

    @JsonProperty("issue_date")
    private String issueDate;


    public String getResult() {
        return result;
    }

    public ResponseDto setResult(String result) {
        this.result = result;
        return this;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public ResponseDto setMessages(List<ValidationMessage> messages) {
        this.messages = messages;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public ResponseDto setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getArchivePdf() {
        return archivePdf;
    }

    public ResponseDto setArchivePdf(String archivePdf) {
        this.archivePdf = archivePdf;
        return this;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public ResponseDto setIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }
}
