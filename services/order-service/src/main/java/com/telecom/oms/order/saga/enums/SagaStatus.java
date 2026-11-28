
package com.telecom.oms.order.saga.enums;

public enum SagaStatus {

    RUNNING,

    COMPENSATING,

    COMPLETED,

    FAILED,

    MANUAL_REVIEW,

    CANCELLED
}
