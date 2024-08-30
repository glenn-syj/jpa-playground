package com.glennsyj.jpaplayground.repository;

import com.glennsyj.jpaplayground.entity.Member;
import com.glennsyj.jpaplayground.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testCreateAndFindMember() {

        // Create a new Member instance using the builder pattern
        Member member = Member.builder()
                .username("test")
                .email("test@test.com")
                .build();

        // Save the Member entity to the database using the repository
        memberRepository.save(member);

        // Retrieve all Members from the database
        List<Member> members = memberRepository.findAll();

        // Verify that the members list is not empty and the username of the first member is "test"
        assertThat(members).isNotEmpty();
        assertThat(members.get(0).getUsername()).isEqualTo("test");

        // Delete the Member entity from the database using the repository
        memberRepository.delete(member);

        // Verify that the Member has been successfully deleted by checking that the ID is no longer present
        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }

}

