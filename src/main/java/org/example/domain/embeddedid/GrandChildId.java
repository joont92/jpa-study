package org.example.domain.embeddedid;

import lombok.EqualsAndHashCode;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@EqualsAndHashCode
public class GrandChildId implements Serializable {
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "parent_id"),
            @JoinColumn(name = "child_id")
    })
    private Child child;

    private String grandChildId;
}
