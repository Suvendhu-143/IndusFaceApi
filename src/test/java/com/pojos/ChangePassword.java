package com.pojos;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor

public class ChangePassword {
    private String username;
    private String password1;
    private String password2;

}
