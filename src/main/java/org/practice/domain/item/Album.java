package org.practice.domain.item;

import lombok.*;
import org.practice.domain.Item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
//@Entity
@DiscriminatorValue("A")
public class Album extends Item {
    public Album(String artist) {
        this.artist = artist;
    }

    private String artist;
    private String etc;
}
