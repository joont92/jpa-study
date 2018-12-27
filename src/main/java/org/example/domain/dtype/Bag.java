package org.example.domain.dtype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "bag")
public class Bag {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "bag")
    private List<Item> itemList = new ArrayList<>();
}
