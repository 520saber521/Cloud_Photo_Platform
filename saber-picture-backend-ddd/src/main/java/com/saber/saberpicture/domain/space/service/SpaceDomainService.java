package com.saber.saberpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.saber.saberpicture.domain.space.entity.Space;
import com.saber.saberpicture.domain.user.entity.User;
import com.saber.saberpicture.interfaces.dto.space.SpaceQueryRequest;

/**
 * @author Saber
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2024-12-18 19:53:34
 */
public interface SpaceDomainService {

    /**
     * 获取查询对象
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充空间对象
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     *
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser, Space space);
}
