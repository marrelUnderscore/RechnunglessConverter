package dev.marrel.rechnunglessconverter;


import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationMessage {

    private String level;

    private String criterion;

    private String location;

    private String type;

    private String message;


    public String getLevel() {
        return level;
    }

    public ValidationMessage setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getCriterion() {
        return criterion;
    }

    public ValidationMessage setCriterion(String criterion) {
        this.criterion = criterion;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public ValidationMessage setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getType() {
        return type;
    }

    public ValidationMessage setType(String type) {
        this.type = type;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ValidationMessage setMessage(String message) {
        this.message = message;
        return this;
    }
}