package org.practice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name="CATEGORY")
public class Category {
    @Id
    @GeneratedValue
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "categoryList")
    private List<Item> itemList = new ArrayList<>();

    public void setParent(Category parent){
        this.parent = parent;

        if(!parent.getChild().contains(this)){
            parent.getChild().add(parent);
        }
    }

    public void addChild(Category child){
        if(!this.child.contains(child)){
            this.child.add(child);
        }

        child.setParent(this);
    }

    public void addItem(Item item){
        if(!this.itemList.contains(item)){
            this.itemList.add(item);
        }

        if(!item.getCategoryList().contains(this)){
            item.getCategoryList().add(this);
        }
    }
}
