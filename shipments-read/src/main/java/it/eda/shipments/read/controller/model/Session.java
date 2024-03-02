package it.eda.shipments.read.controller.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class Session {

    private String sessionId;
    private String email;
    private String role;

}
