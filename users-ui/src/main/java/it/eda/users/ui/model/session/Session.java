package it.eda.users.ui.model.session;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class Session implements Serializable {

    private String sessionId;
    private String email;
    private String role;

}
