package com.heima.behavior.service;


import com.heima.model.behavior.dto.*;


public interface BehaviorService {

    void like(LikeBehaviorDto dto);

    void dislike(DislikeBehaviorDto dto);

    void read(ReadBehaviorDto dto);




}
