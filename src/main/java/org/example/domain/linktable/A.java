package org.example.domain.linktable;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class A {
    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "a")
    private List<B> bList;
}
