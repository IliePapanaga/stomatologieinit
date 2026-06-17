-- Add optional certificates
INSERT IGNORE INTO `certificate_types`(`id`, `optional`)
VALUES
       ('ENDODONTIC_ASSISTANT',   true),
       ('ORAL_SURGERY_ASSISTANT', true),
       ('ORTHODONTIC_ASSISTANT',  true),
       ('PEDODONTIC_ASSISTANT',   true),
       ('PERIODONTAL_ASSISTANT',  true);

-- Add subcategory to certificate mapping
INSERT IGNORE INTO `subcategory_to_certificate_type`(`fk_subcategory_id`, `fk_certificate_type_id`)
VALUES
       ('ENDODONTIC_ASSISTANT',   'ENDODONTIC_ASSISTANT'),
       ('ORAL_SURGERY_ASSISTANT', 'ORAL_SURGERY_ASSISTANT'),
       ('ORTHODONTIC_ASSISTANT',  'ORTHODONTIC_ASSISTANT'),
       ('PEDODONTIC_ASSISTANT',   'PEDODONTIC_ASSISTANT'),
       ('PERIODONTAL_ASSISTANT',  'PERIODONTAL_ASSISTANT');
