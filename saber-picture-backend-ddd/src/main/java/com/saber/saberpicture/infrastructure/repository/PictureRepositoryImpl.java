package com.saber.saberpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saber.saberpicture.domain.picture.entity.Picture;
import com.saber.saberpicture.domain.picture.repository.PictureRepository;
import com.saber.saberpicture.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
 * 图片仓储实现
 */
@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}