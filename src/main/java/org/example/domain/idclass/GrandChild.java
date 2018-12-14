package org.example.domain.idclass;

import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@IdClass(GrandChildId.class)
@Table(name="GRANDCHILD")
public class GrandChild {
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "parent_id"),
            @JoinColumn(name = "child_id")
    })
    private Child child;

    @Id
    private String grandChildId;
}
