package it.eda.users.ui.model.user;

import it.eda.users.ui.model.Dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserBase implements Dto {
    public String email;
    public String password;
}
