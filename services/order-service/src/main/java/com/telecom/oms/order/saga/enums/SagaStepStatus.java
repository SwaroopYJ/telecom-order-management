
package com.telecom.oms.order.saga.enums;

public enum SagaStepStatus {
    PENDING,
    IN_PROGRESS,
    SUCCEEDED,
    FAILED,
    COMPENSATION_PENDING,
    COMPENSATED,
    MANUAL_REVIEW
}
