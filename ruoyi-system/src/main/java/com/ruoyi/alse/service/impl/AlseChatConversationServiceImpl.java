package com.ruoyi.alse.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.mapper.AlseUserMapper;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.uni.model.DTO.respone.message.ConversationListResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseChatConversationMapper;
import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.service.IAlseChatConversationService;

/**
 * 聊天会话Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
@Service
public class AlseChatConversationServiceImpl implements IAlseChatConversationService 
{
    @Autowired
    private AlseChatConversationMapper alseChatConversationMapper;


    @Autowired
    private IAlseUserService alseUserService;


    @Override
    public List<ConversationListResponseDTO> getConversationList(Long userId) {
        // 查询用户相关的所有会话
        List<AlseChatConversation> conversationList = alseChatConversationMapper.selectUserConversations(userId);

        List<ConversationListResponseDTO> resultList = new ArrayList<>();

        for (AlseChatConversation conversation : conversationList) {
            ConversationListResponseDTO dto = new ConversationListResponseDTO();
            dto.setConversationId(conversation.getConversationId());
            dto.setConversationType(conversation.getConversationType());
            dto.setLastMessage(conversation.getLastMessage());
            dto.setLastMessageTime(conversation.getLastMessageTime());

            // 设置未读消息数
            if (userId.equals(conversation.getUserId1())) {
                dto.setUnreadCount(conversation.getUnreadCount1().intValue());
            } else {
                dto.setUnreadCount(conversation.getUnreadCount2().intValue());
            }

            // 如果是单聊，获取对方用户信息
            if (conversation.getConversationType() == 0) { // 0表示单聊
                Long targetUserId = userId.equals(conversation.getUserId1()) ?
                        conversation.getUserId2() : conversation.getUserId1();
                dto.setTargetUserId(targetUserId);

                // 查询对方用户信息
                AlseUser targetUser = alseUserService.selectAlseUserByUserId(targetUserId);
                if (targetUser != null) {
                    dto.setTargetUserName(targetUser.getNickname());
                    dto.setTargetUserAvatar(targetUser.getAvatar());
                }
            } else {
                // 群聊
                dto.setTargetUserName(conversation.getName());
                // 群聊头像可能需要另外处理
            }

            resultList.add(dto);
        }

        return resultList;
    }


    /**
     * 查询聊天会话
     * 
     * @param conversationId 聊天会话主键
     * @return 聊天会话
     */
    @Override
    public AlseChatConversation selectAlseChatConversationByConversationId(Long conversationId)
    {
        return alseChatConversationMapper.selectAlseChatConversationByConversationId(conversationId);
    }

    /**
     * 查询聊天会话列表
     * 
     * @param alseChatConversation 聊天会话
     * @return 聊天会话
     */
    @Override
    public List<AlseChatConversation> selectAlseChatConversationList(AlseChatConversation alseChatConversation)
    {
        return alseChatConversationMapper.selectAlseChatConversationList(alseChatConversation);
    }

    /**
     * 新增聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    @Override
    public int insertAlseChatConversation(AlseChatConversation alseChatConversation)
    {
        alseChatConversation.setCreateTime(DateUtils.getNowDate());
        return alseChatConversationMapper.insertAlseChatConversation(alseChatConversation);
    }

    /**
     * 修改聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    @Override
    public int updateAlseChatConversation(AlseChatConversation alseChatConversation)
    {
        alseChatConversation.setUpdateTime(DateUtils.getNowDate());
        return alseChatConversationMapper.updateAlseChatConversation(alseChatConversation);
    }

    /**
     * 批量删除聊天会话
     * 
     * @param conversationIds 需要删除的聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAlseChatConversationByConversationIds(Long[] conversationIds)
    {
        return alseChatConversationMapper.deleteAlseChatConversationByConversationIds(conversationIds);
    }

    /**
     * 删除聊天会话信息
     * 
     * @param conversationId 聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAlseChatConversationByConversationId(Long conversationId)
    {
        return alseChatConversationMapper.deleteAlseChatConversationByConversationId(conversationId);
    }
}
