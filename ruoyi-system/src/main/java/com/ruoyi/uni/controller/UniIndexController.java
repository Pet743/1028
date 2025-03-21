package com.ruoyi.uni.controller;

import com.ruoyi.alse.domain.AlseIndexContent;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.service.IAlseIndexContentService;
import com.ruoyi.alse.service.IAlseProductService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.uni.model.DTO.respone.index.CarouselDTO;
import com.ruoyi.uni.model.DTO.respone.index.IndexContentResponseDTO;
import com.ruoyi.uni.model.DTO.respone.index.NoticeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页接口Controller
 */
@RestController
@RequestMapping("/api/index")
@Api(tags = "首页信息接口")
@Slf4j
public class UniIndexController {


    @Autowired
    private IAlseIndexContentService indexContentService;

    @Autowired
    private IAlseProductService productService;

    /**
     * 获取首页内容
     */
    @GetMapping("/content")
    @ApiOperation("获取首页内容(轮播图和通知)")
    public AjaxResult getIndexContent() {
        IndexContentResponseDTO responseDTO = new IndexContentResponseDTO();

        // 查询轮播图
        AlseIndexContent carouselQuery = new AlseIndexContent();
        carouselQuery.setContentType(0); // 0-轮播图
        carouselQuery.setStatus("0");    // 0-正常状态
        List<AlseIndexContent> carouselList = indexContentService.selectAlseIndexContentList(carouselQuery);

        // 查询通知公告
        AlseIndexContent noticeQuery = new AlseIndexContent();
        noticeQuery.setContentType(1);   // 1-通知公告
        noticeQuery.setStatus("0");      // 0-正常状态
        List<AlseIndexContent> noticeList = indexContentService.selectAlseIndexContentList(noticeQuery);

        // 转换为响应DTO
        responseDTO.setCarouselList(convertToCarouselDTOList(carouselList));
        responseDTO.setNoticeList(convertToNoticeDTOList(noticeList));

        return AjaxResult.success(responseDTO);
    }

    /**
     * 转换为轮播图DTO列表
     */
    private List<CarouselDTO> convertToCarouselDTOList(List<AlseIndexContent> contentList) {
        if (contentList == null) {
            return new ArrayList<>();
        }

        // 当前时间
        Date now = new Date();

        return contentList.stream()
                .filter(content ->
                        // 过滤掉非有效时间内的内容
                        (content.getStartTime() == null || content.getStartTime().before(now)) &&
                                (content.getEndTime() == null || content.getEndTime().after(now))
                )
                .sorted(Comparator.comparing(AlseIndexContent::getSortOrder))
                .map(content -> {
                    CarouselDTO dto = new CarouselDTO();
                    dto.setContentId(content.getContentId());
                    dto.setTitle(content.getTitle());
                    dto.setImageUrl(content.getImageUrl());
                    dto.setLinkUrl(content.getLinkUrl());
                    dto.setSortOrder(content.getSortOrder());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为通知公告DTO列表
     */
    private List<NoticeDTO> convertToNoticeDTOList(List<AlseIndexContent> contentList) {
        if (contentList == null) {
            return new ArrayList<>();
        }

        // 当前时间
        Date now = new Date();

        return contentList.stream()
                .filter(content ->
                        // 过滤掉非有效时间内的内容
                        (content.getStartTime() == null || content.getStartTime().before(now)) &&
                                (content.getEndTime() == null || content.getEndTime().after(now))
                )
                .sorted(Comparator.comparing(AlseIndexContent::getSortOrder))
                .map(content -> {
                    NoticeDTO dto = new NoticeDTO();
                    dto.setContentId(content.getContentId());
                    dto.setTitle(content.getTitle());
                    dto.setContent(content.getContent());
                    dto.setLinkUrl(content.getLinkUrl());
                    dto.setCreateTime(content.getCreateTime());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    @ApiOperation("搜索商品")
    public AjaxResult searchProducts(@RequestParam String keyword) {
        // 直接调用专门的搜索方法
        List<AlseProduct> productList = productService.searchProducts(keyword);
        return AjaxResult.success(productList);
    }
}


