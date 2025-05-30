package com.glennsyj.jpaplayground.repository;

import com.glennsyj.jpaplayground.entity.TsidMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TsidMemberRepository extends JpaRepository<TsidMember, Long> {

    Optional<TsidMember> findById(Long id);

}
