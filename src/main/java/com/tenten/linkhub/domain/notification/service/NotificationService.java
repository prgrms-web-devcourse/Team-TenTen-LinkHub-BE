package com.tenten.linkhub.domain.notification.service;

import com.tenten.linkhub.domain.notification.model.Notification;
import com.tenten.linkhub.domain.notification.model.NotificationType;
import com.tenten.linkhub.domain.notification.repository.NotificationRepository;
import com.tenten.linkhub.domain.notification.repository.dto.SpaceInvitationNotificationGetDto;
import com.tenten.linkhub.domain.notification.service.dto.SpaceInviteNotificationGetRequest;
import com.tenten.linkhub.domain.notification.service.dto.SpaceInviteNotificationGetResponses;
import com.tenten.linkhub.domain.notification.service.mapper.NotificationMapper;
import com.tenten.linkhub.domain.notification.service.dto.NotificationCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void changeIsCheckedAsTrue(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.getById(notificationId);

        notification.changeIsCheckedAsTrue(memberId);
    }

    public SpaceInviteNotificationGetResponses getSpaceInvitations(SpaceInviteNotificationGetRequest request) {
        Slice<SpaceInvitationNotificationGetDto> notificationGetDtos = notificationRepository.getInviteNotifications(notificationMapper.toQueryCondition(request));
        return SpaceInviteNotificationGetResponses.from(notificationGetDtos);
    }
    public Long createNotification(NotificationCreateRequest request) {

        Notification notification = new Notification(
                request.memberId(),
                request.myMemberId(),
                NotificationType.INVITATION
        );

        return notificationRepository.save(notification).getId();
    }
}
