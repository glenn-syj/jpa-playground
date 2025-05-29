package com.glennsyj.jpaplayground.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tsid_members")
@NoArgsConstructor
@Getter
public class TsidMember {
    @Id
    @Tsid
    private Long id;
}
