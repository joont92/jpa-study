package org.practice.domain;

import lombok.*;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;

import javax.persistence.*;

//@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Table(name = "memo")
@Entity
public class Memo {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "content")
    private String content;

    @AnyMetaDef(
            name = "memoMetaDef",
            idType = "int",
            metaType = "string",
            metaValues = {
                    @MetaValue(value = "M", targetEntity = Member.class),
                    @MetaValue(value = "I", targetEntity = Item.class)
            }
    )
    @Any(metaDef = "memoMetaDef", metaColumn = @Column(name = "resource_type"))
    @JoinColumn(name = "resource_id")
    private Memoable resource;
}
