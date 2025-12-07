package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.pojos.WmNews;

import java.util.Date;

public interface WmScheduleService extends IService<WmNews> {


    void addNewsToTask(Integer id, Date publishTime);

}
