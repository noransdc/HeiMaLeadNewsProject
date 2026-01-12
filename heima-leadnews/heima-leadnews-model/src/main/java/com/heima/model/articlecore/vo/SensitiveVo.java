package com.heima.model.articlecore.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveVo {


    private Long id;
    private String sensitives;
    private LocalDateTime createdTime;

}
