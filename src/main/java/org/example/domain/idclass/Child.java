package org.example.domain.idclass;

import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@IdClass(ChildId.class)
@Table(name="CHILD")
public class Child {
    @Id
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Id
    private String childId;
}
