package api.UsersModels;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreationModel {
    private String firstName;
    private String lastName;
    private int age;
}
