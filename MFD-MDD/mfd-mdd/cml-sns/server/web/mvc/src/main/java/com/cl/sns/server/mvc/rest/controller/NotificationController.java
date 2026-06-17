package com.cl.sns.server.mvc.rest.controller;

import com.cl.sns.server.core.model.api.notification.SendNotificationRequest;
import com.cl.sns.server.core.service.NotificationService;
import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import com.cl.sns.server.mvc.rest.controller.model.notification.SendNotificationRequestDTO;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notification")
public class NotificationController extends BaseController{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AntiCorruptionLayer antiCorruptionLayer;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(AntiCorruptionLayer antiCorruptionLayer,
                                  NotificationService notificationService){
        this.antiCorruptionLayer = antiCorruptionLayer;
        this.notificationService = notificationService;
    }

    /**
     * Send notification
     * <p />
     *
     */
    @PostMapping("send")
    public ResponseEntity<ResponseDTO> send(@RequestBody @Valid SendNotificationRequestDTO requestDTO){
        Validate.notNull(requestDTO, "Invalid send notification request");
        SendNotificationRequest notificationRequest = antiCorruptionLayer.convert(requestDTO);
        notificationService.send(notificationRequest);

        return wrapResult();
    }


}