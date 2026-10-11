package com.saber.saberpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saber.saberpicture.domain.space.entity.SpaceUser;
import com.saber.saberpicture.domain.space.repository.SpaceUserRepository;
import com.saber.saberpicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

/**
 * 空间用户仓储实现
 */
@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}