package com.cl.sns.server.mvc.rest.controller;

import com.cl.sns.server.core.model.api.template.NotificationTemplateModel;
import com.cl.sns.server.core.model.api.template.NotificationTemplateModelList;
import com.cl.sns.server.core.service.NotificationTemplateService;
import com.cl.sns.server.mvc.rest.controller.model.common.ResponseDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.CreateNotificationTemplateDTO;
import com.cl.sns.server.mvc.rest.controller.model.templates.NotificationTemplateDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notification/template")
public class NotificationTemplateController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationTemplateService ntfTemplateService;
    @Autowired
    private AntiCorruptionLayer antiCorruptionLayer;

    /**
     * Save Notification Template operation.
     * <p />
     * @param createNotificationTemplateDTO
     * @return created notification template
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> save(@RequestBody CreateNotificationTemplateDTO createNotificationTemplateDTO) {
        Validate.notNull(createNotificationTemplateDTO);
        logger.debug("Create notification template request : {} ", createNotificationTemplateDTO);
        NotificationTemplateModel saved = ntfTemplateService.save(antiCorruptionLayer.convert(createNotificationTemplateDTO));

        return wrapResult(antiCorruptionLayer.convert(saved));
    }

    /**
     * Update existing notification template operation.
     * <p />
     * @param notificationTemplateDTO
     * @return updated notification template
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@RequestBody NotificationTemplateDTO notificationTemplateDTO) {
        Validate.notNull(notificationTemplateDTO);

        logger.debug("Update notification template request : {} ", notificationTemplateDTO);
        NotificationTemplateModel saved = ntfTemplateService.update(antiCorruptionLayer.convert(notificationTemplateDTO));

        return wrapResult(antiCorruptionLayer.convert(saved));
    }

    /**
     * Get notification template by id.
     * <p/>
     *
     * @param id - notification template id
     * @return notification template if found
     */
    @GetMapping("{templateID}")
    public ResponseEntity<ResponseDTO> get(@PathVariable("templateID") String id) {
        Validate.notBlank(id, "Invalid notification template id");
        logger.debug("Get notification template by id request: {} ", id);
        NotificationTemplateModel notificationTemplateModel = ntfTemplateService.get(id);
        return wrapResult(antiCorruptionLayer.convert(notificationTemplateModel));
    }

    /**
     * Delete Notification Template.
     * <p/>
     * @param id - notification template id
     */
    @DeleteMapping("{templateID}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable("templateID") String id) {
        Validate.notBlank(id, "Invalid notification template id");
        logger.debug("Delete notification template by id request: {} ", id);
        ntfTemplateService.delete(id);
        return wrapResult();
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> list(@RequestParam(name = "sort_by", required = false) String sortString,
                                            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "perPage", required = false, defaultValue = "25") Integer perPage) {
        Pageable pageable = createPageable(sortString, page, perPage);
        NotificationTemplateModelList result = ntfTemplateService.list(pageable);
        return wrapResult(antiCorruptionLayer.convert(result));
    }

    private Pageable createPageable(String sortString, Integer page, Integer perPage) {
        List<Sort.Order> orders = new ArrayList<>();

        if (StringUtils.isNotBlank(sortString)) {
            String[] orderTokens = sortString.split(",");

            for (String orderToken : orderTokens) {
                validateOrderToken(orderToken);

                orders.add(convertToOrder(orderToken));
            }
        }

        return new PageRequest(page, perPage, new Sort(orders));
    }

    private Sort.Order convertToOrder(String orderToken) {
        validateOrderToken(orderToken);
        return new Sort.Order(orderToken.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC, orderToken.substring(1));
    }

    private void validateOrderToken(String orderToken) {
        if (!orderToken.startsWith("-") && !orderToken.startsWith("+")) {
            throw new IllegalArgumentException("Sorting token should start either from '+' or '-'");
        }

        if (orderToken.length() <= 1) {
            throw new IllegalArgumentException("Sorting token should contain definition of sort field");
        }
    }
}