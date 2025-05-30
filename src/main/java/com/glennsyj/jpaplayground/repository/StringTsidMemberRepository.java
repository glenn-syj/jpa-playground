package com.glennsyj.jpaplayground.repository;

import com.glennsyj.jpaplayground.entity.StringTsidMember;
import com.glennsyj.jpaplayground.entity.TsidMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringTsidMemberRepository extends JpaRepository<StringTsidMember, String> {
}
