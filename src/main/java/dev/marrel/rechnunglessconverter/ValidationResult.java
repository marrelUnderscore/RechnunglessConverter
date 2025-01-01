package dev.marrel.rechnunglessconverter;

import dev.marrel.rechnunglessconverter.util.XMLTools;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;


public class ValidationResult {

    private boolean isValid = false;

    private List<ValidationMessage> messages = new ArrayList<>();


    public ValidationResult(String mustangValidationXml) {
        Element e = XMLTools.parseStringXML(mustangValidationXml);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        try {
            isValid = xpath.evaluate("/validation/summary/@status", e).equals("valid");
        } catch (XPathExpressionException ex) {
            throw new RuntimeException(ex);
        }

        NodeList messageContainers = e.getElementsByTagName("messages");
        for (int i = 0; i < messageContainers.getLength(); i++) {
            Node messageContainer = messageContainers.item(i);
            NodeList messageItems = messageContainer.getChildNodes();
            for (int k = 0; k < messageItems.getLength(); k++) {
                Node messageItem = messageItems.item(k);
                if ("#text".equals(messageItem.getNodeName())) {
                    continue;
                }
                ValidationMessage validationMessage = new ValidationMessage()
                        .setLevel(messageItem.getNodeName())
                        .setMessage(messageItem.getTextContent().trim());
                if (messageItem instanceof Element messageElement) {
                    validationMessage
                            .setCriterion(messageElement.getAttribute("criterion"))
                            .setLocation(messageElement.getAttribute("location"))
                            .setType(messageElement.getAttribute("type"));
                }
                if (!validationMessage.getMessage().isBlank()) {
                    messages.add(validationMessage);
                }
            }
        }
    }


    public boolean isValid() {
        return isValid;
    }


    public List<ValidationMessage> getMessages() {
        return messages;
    }
}
