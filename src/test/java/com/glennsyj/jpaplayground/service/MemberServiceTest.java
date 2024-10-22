package com.glennsyj.jpaplayground.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.glennsyj.jpaplayground.entity.Member;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("Eager 방식으로, 해당 username을 가진 멤버가 존재할 때, 기존 멤버 정보를 가져와야 함")
	public void should_ReturnExistingMember_WhenMemberExists_Eagerly() throws InterruptedException {
		// given
		// Assert that any member with current username does not exist in DB.
		String username = "testUser";
		setupMemberIfNotExists(username);

		// when
		Member result = memberService.getMemberByNameWithDefaultsEagerly(username);

		// then
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		assertEquals(username + "@jpa.playground", result.getEmail());
	}

	@Test
	@DisplayName("Lazy 방식으로, 해당 username을 가진 멤버가 존재할 때, 기존 멤버 정보를 가져와야 함")
	public void should_ReturnExistingMember_WhenMemberExists_Lazily() throws InterruptedException {
		// given
		// Assert that any member with current username does not exist in DB.
		String username = "testUser";
		setupMemberIfNotExists(username);

		// when
		Member result = memberService.getMemberByNameWithDefaultsLazily(username);
		// then
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		assertEquals(username + "@jpa.playground", result.getEmail());
	}

	private void setupMemberIfNotExists(String username) {
		if (memberService.getMemberByUsername(username) == null) {
			Member newMember = Member.builder().username(username).email(username + "@jpa.playground").build();
			memberService.createMember(newMember);
		}
	}

}
