package org.example.domain.linktable;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class B {
    @Id
    private String id;

    @ManyToOne
    @JoinTable(name = "a_b",
            joinColumns = @JoinColumn(name = "a_id"),
            inverseJoinColumns = @JoinColumn(name = "b_id"))
    private A a;
}
