package com.example.pass.repository.pass;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.pass.repository.pass.BulkPassStatus.COMPLETED;
import static com.example.pass.repository.pass.PassStatus.READY;
import static org.junit.jupiter.api.Assertions.*;

class PassModelMapperTest {

    @Test
    void test_toPassEntity() {
        // given
        final LocalDateTime now = LocalDateTime.now();
        final String userId = "A10000000";

        BulkPassEntity bulkPassEntity = new BulkPassEntity();
        bulkPassEntity.setPackageSeq(1);
        bulkPassEntity.setUserGroupId("GROUP");
        bulkPassEntity.setStatus(COMPLETED);
        bulkPassEntity.setCount(10);
        bulkPassEntity.setStartedAt(now.minusDays(60));
        bulkPassEntity.setEndedAt(now);

        // when
        final PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);

        // then
        assertEquals(1, passEntity.getPackageSeq());
        assertEquals(READY, passEntity.getStatus());
        assertEquals(10, passEntity.getRemainingCount());
        assertEquals(now.minusDays(60), passEntity.getStartedAt());
        assertEquals(now, passEntity.getEndedAt());
        assertEquals(userId, passEntity.getUserId());
    }
}