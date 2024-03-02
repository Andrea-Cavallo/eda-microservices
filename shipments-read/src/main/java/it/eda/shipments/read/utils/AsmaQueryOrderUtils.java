package it.eda.shipments.read.utils;

import it.eda.shipments.read.controller.model.RestResponse;
import it.eda.shipments.read.controller.model.Shipments;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AsmaQueryOrderUtils {

    private AsmaQueryOrderUtils() {
    }


    /**
     * Checks if a field is missing or empty.
     *
     * @param field The field to check.
     * @return {@code true} if the field is null or an empty string, {@code false} otherwise.
     */
    public static boolean isFieldMissing(Object field) {
        return field == null || (field instanceof String && ((String) field).isEmpty());
    }

    /**
     * Checks if the given email is valid.
     *
     * @param email The email address to validate.
     * @return true if the email is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }




    /**
     * Builds a RestResponse object with error messages.
     *
     * @param errorMessage The error message.
     * @param codice       The error code.
     * @param httpStatus   The HTTP status code.
     * @return A RestResponse object with the specified error message, error code, and HTTP status code.
     */
    public static RestResponse<Shipments> buildRestResponseWithErrorMessages(String errorMessage, Long codice, Long httpStatus ) {
        RestResponse<Shipments> response = new RestResponse<>();
        response.setOutput(null);
        response.setDescrizioneErrore(Map.of("errorMessage", errorMessage));
        response.setCodiceErrore(Map.of("codice",codice));
        response.setCodiceErrore(Map.of("httpStatusCode",httpStatus));

        return response;
    }

    /**
     * Builds a successful response containing a list of shipments.
     *
     * @param shipmentList the list of shipments to include in the response
     * @return a RestResponse object containing the shipment list
     */
    public static RestResponse<List<Shipments>> buildSuccesfulResponse(List<Shipments> shipmentList) {
        RestResponse<List<Shipments>> response = new RestResponse<>();
        response.setOutput(shipmentList);
        response.setDescrizioneErrore(Collections.emptyMap());
        response.setDescrizioneErrore(Collections.emptyMap());
        return response;

    }




    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@"
            + "[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";




}
