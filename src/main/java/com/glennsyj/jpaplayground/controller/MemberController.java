package com.glennsyj.jpaplayground.controller;

import com.glennsyj.jpaplayground.entity.TsidMember;
import com.glennsyj.jpaplayground.model.TsidMemberLongDto;
import com.glennsyj.jpaplayground.model.TsidMemberCreateDto;
import com.glennsyj.jpaplayground.model.TsidMemberStringDto;
import com.glennsyj.jpaplayground.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;
    private final Logger log = LoggerFactory.getLogger("MemberController");

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/tsid/{tsid}")
    public ResponseEntity<?> getTsidMember(@PathVariable Long tsid) {
        try {
            log.info("PathVariable tsid: {}", tsid);
            TsidMember tsidMember = memberService.getTsidMemberFrom(tsid);

            TsidMemberLongDto dto = new TsidMemberLongDto(tsidMember.getId(), tsidMember.getName());

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/tsid")
    public ResponseEntity<?> createTsidMember(@RequestBody TsidMemberCreateDto dto) {
        try {
            TsidMember tsidMember = memberService.createTsidMember(dto.name());
            log.info("TsidMember id: {} and name: {}", tsidMember.getId(), tsidMember.getName());

            Object responseDto = switch (dto.type().toUpperCase()) {
                case "LONG" -> new TsidMemberLongDto(tsidMember.getId(), tsidMember.getName());
                case "STRING" -> new TsidMemberStringDto(tsidMember.getId().toString(), tsidMember.getName());
                default -> throw new IllegalArgumentException("Wrong TsidMemberCreationDto Type");
            };

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
