package com.saber.saberpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.saber.saberpicture.domain.space.entity.SpaceUser;
import com.saber.saberpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;

/**
 * @author Saber
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2025-01-02 20:07:15
 */
public interface SpaceUserDomainService {

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}
