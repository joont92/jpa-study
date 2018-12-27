package org.example.domain.dtype;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "movie")
//@DiscriminatorValue("M")
public class Movie extends Item{
    private String actor;
}
