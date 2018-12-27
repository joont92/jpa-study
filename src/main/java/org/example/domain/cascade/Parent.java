package org.example.domain.cascade;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
@Getter
public class Parent {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy =  "parent", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child){
        this.childList.add(child);
        child.setParent(this);
    }
}
