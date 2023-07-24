INSERT INTO package (package_name, count, period, created_at)
VALUES ('Starter PT 10회', 10, 60, NOW()),
       ('Starter PT 10회', 20, 120, NOW()),
       ('Starter PT 10회', 30, 180, NOW()),
       ('무료 이벤트 필라테스 1회', 1, NULL, NOW()),
       ('바디 챌린지 PT 4주', NULL, 28, NOW()),
       ('바디 챌린지 PT 8주', NULL, 48, NOW()),
       ('인바디 상담', NULL, NULL, NOW());

INSERT INTO `user` (user_id, user_name, status, phone, meta, created_at)
VALUES ('A10000000', '우영우', 'ACTIVE', '01011112222', NULL, NOW()),
       ('A10000001', '최수연', 'ACTIVE', '01033334444', NULL, NOW()),
       ('A10000002', '이준호', 'INACTIVE', '01055556666', NULL, NOW()),
       ('B10000010', '권민우', 'ACTIVE', '01077778888', NULL, NOW()),
       ('B10000011', '동그라미', 'INACTIVE', '01088889999', NULL, NOW()),
       ('B20000000', '한선영', 'ACTIVE', '01099990000', NULL, NOW()),
       ('B20000001', '태수미', 'ACTIVE', '01000001111', NULL, NOW());

INSERT INTO user_group_mapping (user_group_id, user_id, user_group_name, description, created_at)
VALUES ('HANBADA', 'A10000000', '한바다', '한바다 임직원 그룹', NOW()),
       ('HANBADA', 'A10000001', '한바다', '한바다 임직원 그룹', NOW()),
       ('HANBADA', 'A10000002', '한바다', '한바다 임직원 그룹', NOW()),
       ('HANBADA', 'B10000010', '한바다', '한바다 임직원 그룹', NOW()),
       ('HANBADA', 'B20000000', '한바다', '한바다 임직원 그룹', NOW()),
       ('TEASAN', 'B20000001', '태산', '태산 임직원 그룹', NOW());
