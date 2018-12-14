package org.example.domain.embeddedid;

import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name="GRANDCHILD")
public class GrandChild {
    @EmbeddedId
    private GrandChildId grandChildId;
}
