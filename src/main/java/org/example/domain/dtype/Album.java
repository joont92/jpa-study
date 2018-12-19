package org.example.domain.dtype;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "album")
//@DiscriminatorValue("A")
public class Album extends Item{
    private String author;
}
