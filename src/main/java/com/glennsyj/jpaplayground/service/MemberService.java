package com.glennsyj.jpaplayground.service;

import com.glennsyj.jpaplayground.entity.Member;
import com.glennsyj.jpaplayground.entity.TsidMember;
import com.glennsyj.jpaplayground.repository.MemberRepository;
import com.glennsyj.jpaplayground.repository.TsidMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TsidMemberRepository tsidMemberRepository;

    public MemberService(MemberRepository memberRepository, TsidMemberRepository tsidMemberRepository) {
        this.memberRepository = memberRepository;
        this.tsidMemberRepository = tsidMemberRepository;
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public TsidMember getTsidMemberFrom(Long id) {
        return tsidMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No TsidMember form the id based on tsid"));
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public void deleteAllMember() {
        memberRepository.deleteAll();
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public Member getMemberByNameWithDefaultsEagerly(String username) {
        return memberRepository.findByUsername(username)
            .orElse(memberRepository.save(Member.builder().username(username).email(username+"@jpa.playground").build()));
    }

    public Member getMemberByNameWithDefaultsLazily(String username) {
        return memberRepository.findByUsername(username).orElseGet(
            () -> memberRepository.save(Member.builder().username(username).email(username+"@jpa.playground").build()));
    }

}

