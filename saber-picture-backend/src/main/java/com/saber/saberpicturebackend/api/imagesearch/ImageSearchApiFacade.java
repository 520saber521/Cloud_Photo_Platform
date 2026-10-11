package com.saber.saberpicturebackend.api.imagesearch;

import com.saber.saberpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.saber.saberpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.saber.saberpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.saber.saberpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        List<ImageSearchResult> imageList = searchImage("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        System.out.println("结果列表" + imageList);
    }
}
