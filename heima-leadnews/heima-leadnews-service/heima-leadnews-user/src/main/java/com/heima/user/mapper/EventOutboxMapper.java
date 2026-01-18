package com.heima.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.user.entity.EventOutbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface EventOutboxMapper extends BaseMapper<EventOutbox> {


    List<EventOutbox> listByStatus(@Param("status") int status, @Param("limit") int limit);

    int markSending(@Param("eventId") String eventId,
                     @Param("targetStatus") int targetStatus,
                     @Param("originalStatus") int originalStatus);

    void markSent(@Param("eventId") String eventId,
                     @Param("targetStatus") int targetStatus,
                     @Param("originalStatus") int originalStatus);

    void markFailed(@Param("eventId") String eventId,
                  @Param("targetStatus") int targetStatus,
                  @Param("originalStatus") int originalStatus);

}
