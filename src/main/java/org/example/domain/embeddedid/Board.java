package org.example.domain.embeddedid;

import javax.persistence.*;

@Entity
@Table(name = "board")
@SecondaryTable(name = "board_detail", // 1
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "board_detail_id")) // 2
public class Board{
    @Id
    private Long boardId;

    private String title;

    @Column(table = "board_detail") // 3
    private String content;
}
