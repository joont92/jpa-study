package org.example.domain.cascade;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy =  "child", cascade = CascadeType.ALL)
    private List<GrandChild> grandChildList = new ArrayList<>();

    public void addGrandChild(GrandChild grandChild){
        this.grandChildList.add(grandChild);
        grandChild.setChild(this);
    }
}
