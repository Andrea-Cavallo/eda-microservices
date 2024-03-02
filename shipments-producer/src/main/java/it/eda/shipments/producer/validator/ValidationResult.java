package it.eda.shipments.producer.validator;

import io.quarkus.arc.All;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ValidationResult {
    private boolean isValid;
    private List<String> errors;

}