package academy.devdojo.youtube.core.model;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Course implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The field 'title' is mandatory")
    @Column(nullable = false)
    private String title;

}
