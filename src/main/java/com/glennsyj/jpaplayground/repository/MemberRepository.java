package com.glennsyj.jpaplayground.repository;

import java.util.List;
import java.util.Optional;

import com.glennsyj.jpaplayground.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsername(String username);

	List<Member> findAllByUsername(String username);

}