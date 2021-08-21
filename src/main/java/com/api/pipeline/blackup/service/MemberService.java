package com.api.pipeline.blackup.service;

import com.api.pipeline.blackup.dto.MemberDto;
import com.api.pipeline.blackup.entity.MemberEntity;
import com.api.pipeline.blackup.repository.MemberRepository;
import com.api.pipeline.main.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    public boolean createMember (MemberEntity member) {
        try {
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            System.out.println("createMember Exception=>" + e);
            return false;
        }

    }

    @Transactional
    public Optional<MemberEntity> getUserWithAuthorities(String id) {
        return memberRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional
    public Optional<MemberEntity> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesById);
    }

    @Transactional
    public MemberEntity signup(MemberDto memberDto) {
        if (memberRepository.findById(memberDto.getId()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

//        Authority authority = Authority.builder()
//                .authorityName("ROLE_USER")
//                .build();
        System.out.println("dto" + memberDto.getId() + passwordEncoder.encode(memberDto.getPassword()));
        MemberEntity member = MemberEntity.builder()
                .id(memberDto.getId())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .register_at(new Date())
                .build();

        return memberRepository.save(member);
    }

    public Optional<MemberEntity> getMember(Integer mem_id){
        return memberRepository.findById(mem_id);
    }

    public MemberEntity updateMember(Integer mem_id, MemberEntity member){
        final Optional<MemberEntity> fetchedMember = memberRepository.findById(mem_id);
        if(fetchedMember.isPresent()){
            member.setMem_id(mem_id);
            if(fetchedMember.isEmpty() != false){
                fetchedMember.get().setName(member.getName());
            }
            return memberRepository.save(member);
        }
        else{
            return null;
        }
    }

    public boolean deleteMember(Integer mem_id){
        final Optional<MemberEntity> fetchedMember = memberRepository.findById(mem_id);
        if(fetchedMember.isPresent()){
            memberRepository.deleteById(mem_id);
            return true;
        }
        else{
            return false;
        }
    }
}
