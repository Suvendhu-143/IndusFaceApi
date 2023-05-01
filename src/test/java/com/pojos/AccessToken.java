

package com.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {

    @JsonProperty("Authorization")
    private String authorization;
    private String success;
}

