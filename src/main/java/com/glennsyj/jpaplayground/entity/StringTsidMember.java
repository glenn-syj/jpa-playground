package com.glennsyj.jpaplayground.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "string_tsid_members")
@NoArgsConstructor
@Getter
public class StringTsidMember {
    @Id
    @Tsid
    private String id;

    private String name;

    @Builder
    public StringTsidMember(String name) {
        this.name = name;
    }
}
