package com.martin.tube.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Tag parentTag;

    @JsonIgnore
    @OneToMany(mappedBy = "parentTag")
    private Set<Tag> subTags;

    @Override
    public boolean equals(Object obj){
        if (obj == null || this.id == null)
            return false;

        if (this == obj)
            return true;

        if (this.getClass() != obj.getClass())
            return false;

        Tag tag = (Tag) obj;

        return this.id.equals(tag.getId());
    }


    @Override
    public int hashCode(){
        int hash = 7;
        hash = hash * 31 + (id == null ? 0 : id.hashCode());

        return hash;
    }
}
