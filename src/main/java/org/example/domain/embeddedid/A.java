package org.example.domain.embeddedid;

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
    private String aId;

    @OneToMany
    @JoinTable(name = "a_b",
            joinColumns = @JoinColumn(name = "a_id"),
            inverseJoinColumns = @JoinColumn(name = "b_id"))
    private List<B> bList = new ArrayList<>();
}
