package dev.marrel.rechnunglessconverter;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class InvalidXRechnungException extends RuntimeException {

    private List<ValidationMessage> validationMessages = null;

    InvalidXRechnungException(String message) {
        super(message);
        this.validationMessages = Collections.singletonList(new ValidationMessage().setMessage(message));
    }

    InvalidXRechnungException(ValidationResult validationResult) {
        super(validationResult.getMessages().stream().map(vm -> vm.getLevel() + ": " + vm.getMessage()).collect(Collectors.joining("\n")));
        this.validationMessages = validationResult.getMessages();
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}
