package it.eda.users.ui.model.user;

import lombok.*;

@Getter@Setter@NoArgsConstructor
@AllArgsConstructor@ToString
@EqualsAndHashCode(callSuper = true )
public class User  extends UserBase{
    private Long id;
    private String nome;
    private String cognome;

}
