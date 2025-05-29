package com.glennsyj.jpaplayground.lock;

import com.glennsyj.jpaplayground.entity.Member;
import com.glennsyj.jpaplayground.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class OptimisticLockTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void testOptimisticLocking() throws InterruptedException {

        // When
        Member member = Member.builder()
                .username("glenn")
                .email("glenn@example.com")
                .build();
        memberRepository.save(member);

        Runnable task = () -> {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.execute(status -> {
                Member memberToUpdate = memberRepository.findById(member.getId()).orElseThrow();
                memberToUpdate.updateEmail(memberToUpdate.getEmail() + ".update");
                try {
                    Thread.sleep(100); // Simulate some processing time
                    memberRepository.save(memberToUpdate);
                } catch (OptimisticLockingFailureException e) {
                    System.out.println("OptimisticLockingFailureException caught: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        Member updatedMember = memberRepository.findById(member.getId()).get();
        System.out.println("Final Email: " + updatedMember.getEmail());
    }
}
