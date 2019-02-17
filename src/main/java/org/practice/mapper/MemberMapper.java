package org.practice.mapper;

import org.mapstruct.Mapper;
import org.practice.domain.Member;
import org.practice.dto.MemberDTO;

@Mapper
public interface MemberMapper {
    MemberDTO toDTO(Member member);
}
