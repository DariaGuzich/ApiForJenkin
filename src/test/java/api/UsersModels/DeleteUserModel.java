package api.UsersModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteUserModel {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    
    private String deletedOn;
}