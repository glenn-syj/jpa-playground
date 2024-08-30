package com.glennsyj.jpaplayground.repository;

import com.glennsyj.jpaplayground.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}