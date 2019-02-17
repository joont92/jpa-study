package org.practice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class MemberDTO {
    private Integer id;
    private String name;
    private List<OrderDTO> order;
}
