package com.glennsyj.jpaplayground.manager;

import com.glennsyj.jpaplayground.entity.Member;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class MemberEntityManagerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testEntityManagerIsSession() {
        // Null check needed
        assertThat(entityManager).isNotNull();

        // Assuming entityManager is properly initialized
        assertThat(entityManager).isInstanceOf(Session.class);

        // Cast to Session
        Session session = (Session) entityManager;
    }

    @Test
    public void testCreateAndFindMember() {
        // Create a Member
        Member member = Member.builder()
                .username("test")
                .email("test@example.com")
                .build();

        if (entityManager instanceof Session) {
            Session session = (Session) entityManager;
            System.out.println("EntityManager is a Hibernate Session.");
            System.out.println("Session class: " + session.getClass().getName());
        } else {
            System.out.println("EntityManager is not a Hibernate Session.");
        }

        // Persist the entity
        entityManager.persist(member);
        entityManager.flush(); // Force the changes to be applied to the database

        // Find the Member
        Member foundMember = entityManager.find(Member.class, member.getId());
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getUsername()).isEqualTo("test");

        // Retrieve all Members using JPQL
        List<Member> members = entityManager.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
        assertThat(members).isNotEmpty();

        // Remove the entity
        entityManager.remove(foundMember);
        entityManager.flush();

        // Verify that the Member has been deleted
        Member deletedMember = entityManager.find(Member.class, member.getId());
        assertThat(deletedMember).isNull();
    }

}
