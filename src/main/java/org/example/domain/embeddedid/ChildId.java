package org.example.domain.embeddedid;

import lombok.EqualsAndHashCode;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@EqualsAndHashCode
public class ChildId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    private String childId;
}
