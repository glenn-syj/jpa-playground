package com.glennsyj.jpaplayground;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.glennsyj.jpaplayground.entity.StringTsidMember;
import com.glennsyj.jpaplayground.entity.TsidMember;
import com.glennsyj.jpaplayground.model.TsidMemberLongDto;
import com.glennsyj.jpaplayground.model.TsidMemberStringDto;
import com.glennsyj.jpaplayground.repository.StringTsidMemberRepository;
import com.glennsyj.jpaplayground.repository.TsidMemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class  JsonTsidPerformanceTest {

    @Autowired
    private TsidMemberRepository tsidMemberRepository;

    @Autowired
    private StringTsidMemberRepository stringTsidMemberRepository;

    @Autowired
    private EntityManager entityManager;

    private static ObjectMapper defaultMapper;
    private static ObjectMapper longToStringMapper;

    // TEST 사이즈 조정 위함
    private final int TEST_SIZE = 200_000;
    // 조회 시 EntityManager 1차 캐시 사용 여부: entityManager.flush() && clear()
    private boolean ALLOW_FIRST_CACHE = false;

    @BeforeAll
    static void setup() {
        defaultMapper = new ObjectMapper();

        longToStringMapper = JsonMapper.builder()
                .addModule(new SimpleModule() {{
                    addSerializer(Long.class, ToStringSerializer.instance);
                    addSerializer(Long.TYPE, ToStringSerializer.instance);
                }})
                .build();
    }

    /**
     * 1. 엔티티 ID가 String 타입인 경우 (StringTsidMember)
     */
    @Test
    @Order(1)
    @DisplayName("1. 엔티티 ID String - 객체 생성 및 DB 성능 테스트")
    void entityStringIdPerformanceTest() {

        warmupEntities(
                1000,
                () -> StringTsidMember.builder().name("warmup").build(),
                StringTsidMember::getId,
                stringTsidMemberRepository
        );

        // 객체 생성 시간 측정
        long startCreate = System.nanoTime();
        List<StringTsidMember> entities = new ArrayList<>(TEST_SIZE);
        for (int i = 0; i < TEST_SIZE; i++) {
            entities.add(StringTsidMember.builder()
                    .name("test" + i)
                    .build());
        }
        long endCreate = System.nanoTime();

        System.out.printf("[1] 객체 생성 시간: %d ms%n", (endCreate - startCreate) / 1_000_000);

        // DB insert 시간 측정
        long startInsert = System.nanoTime();
        List<StringTsidMember> saved = stringTsidMemberRepository.saveAll(entities);
        if (!ALLOW_FIRST_CACHE) {
            entityManager.flush();
        }
        long endInsert = System.nanoTime();
        System.out.printf("[1] DB 저장 시간: %d ms%n", (endInsert - startInsert) / 1_000_000);

        // 1차 캐시 없는 DB 조회를 위한 작업
        String targetId = saved.get(TEST_SIZE / 2).getId();
        if (!ALLOW_FIRST_CACHE) {
            entityManager.clear();
        }

        // DB 조회 시간 측정
        long startSelect = System.nanoTime();
        Optional<StringTsidMember> fetched = stringTsidMemberRepository.findById(targetId);
        assert fetched.isPresent();
        long endSelect = System.nanoTime();
        System.out.printf("[1] DB 조회 시간: %d ms%n", (endSelect - startSelect) / 1_000_000);

        assertEquals(TEST_SIZE, saved.size());
    }

    /**
     * 2. 엔티티 ID는 Long, DTO에서만 String 사용 (TsidMember, TsidMemberStringDto)
     */
    @Test
    @Order(2)
    @DisplayName("2. 엔티티 ID Long + DTO String - 객체 생성 및 DB 성능 테스트")
    void entityLongIdDtoStringPerformanceTest() throws JsonProcessingException {

        warmupEntities(
                1000,
                () -> TsidMember.builder().name("warmup").build(),
                TsidMember::getId,
                tsidMemberRepository
        );

        // 객체 생성 및 DB 저장
        long startCreate = System.nanoTime();
        List<TsidMember> entities = new ArrayList<>(TEST_SIZE);
        for (int i = 0; i < TEST_SIZE; i++) {
            entities.add(TsidMember.builder()
                    .name("test" + i)
                    .build());
        }
        long endCreate = System.nanoTime();
        System.out.printf("[2] 객체 생성 시간 (저장 전): %d ms%n", (endCreate - startCreate) / 1_000_000);

        long startInsert = System.nanoTime();
        List<TsidMember> saved = tsidMemberRepository.saveAll(entities);
        if (!ALLOW_FIRST_CACHE) {
            entityManager.flush();
        }
        long endInsert = System.nanoTime();
        System.out.printf("[2] DB 저장 시간: %d ms%n", (endInsert - startInsert) / 1_000_000);

        // 1차 캐시 없는 DB 조회를 위한 작업
        Long targetId = saved.get(TEST_SIZE / 2).getId();
        if (!ALLOW_FIRST_CACHE) {
            entityManager.clear();
        }

        // DB 조회
        long startSelect = System.nanoTime();
        Optional<TsidMember> fetched = tsidMemberRepository.findById(targetId);
        assert fetched.isPresent();
        long endSelect = System.nanoTime();
        System.out.printf("[2] DB 조회 시간: %d ms%n", (endSelect - startSelect) / 1_000_000);

        assertEquals(TEST_SIZE, saved.size());

        // DTO 변환 및 JSON 직렬화 테스트
        long startDtoJson = System.nanoTime();
        for (TsidMember member : saved) {
            // String 타입 DTO 사용
            String json = defaultMapper.writeValueAsString(
                    new TsidMemberStringDto(member.getId().toString(), member.getName())
            );
            TsidMemberStringDto dto = defaultMapper.readValue(json, TsidMemberStringDto.class);
            assertEquals(member.getId().toString(), dto.id());
        }
        long endDtoJson = System.nanoTime();
        System.out.printf("[2] DTO 변환 + JSON 직렬화 시간: %d ms%n", (endDtoJson - startDtoJson) / 1_000_000);
    }

    /**
     * 3. 엔티티 ID Long, Jackson 설정으로 Long -> String 직렬화 (TsidMember, TsidMemberLongDto)
     *  - 객체 생성은 엔티티 Long 기준, JSON 직렬화 성능만 측정
     */
    @Test
    @Order(3)
    @DisplayName("3. Jackson Long->String 설정 직렬화 성능 테스트")
    void jacksonLongToStringSerializationTest() throws Exception {
        // 객체 생성 (Long 엔티티 기준)
        long startCreate = System.nanoTime();
        List<TsidMember> entities = new ArrayList<>(TEST_SIZE);
        for (int i = 0; i < TEST_SIZE; i++) {
            entities.add(TsidMember.builder()
                    .name("test" + i)
                    .build());
        }
        long endCreate = System.nanoTime();
        System.out.printf("[3] 객체 생성 시간: %d ms%n", (endCreate - startCreate) / 1_000_000);

        // JSON 직렬화 성능 측정 (저장 없이 메모리 기준)
        long startJson = System.nanoTime();
        for (TsidMember member : entities) {
            String json = longToStringMapper.writeValueAsString(
                    new TsidMemberLongDto(member.getId(), member.getName())
            );
            TsidMemberLongDto dto = longToStringMapper.readValue(json, TsidMemberLongDto.class);
        }
        long endJson = System.nanoTime();
        System.out.printf("[3] Jackson Long->String 직렬화 시간: %d ms%n", (endJson - startJson) / 1_000_000);
    }

    public <T, ID> void warmupEntities(
            int count,
            Supplier<T> entitySupplier,
            Function<T, ID> getIdFunction,
            JpaRepository<T, ID> repository
    ) {
        List<T> entities = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entities.add(entitySupplier.get());
        }

        // 저장
        repository.saveAll(entities);

        // 1차 캐시 클리어
        entityManager.clear();

        // 조회
        for (T entity : entities) {
            ID id = getIdFunction.apply(entity);
            repository.findById(id);
        }

        // 다시 1차 캐시 클리어
        entityManager.clear();
    }
}
