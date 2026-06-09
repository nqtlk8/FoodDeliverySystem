package com.order.order_service.entity;

public enum OrderStatus {
    PENDING_VALIDATION,    // FR-ORD-01: Chờ xác thực
    PENDING_ADJUSTMENT,    // FR-ORD-06: Chờ điều chỉnh (Voucher bị từ chối)
    WAITING_FOR_DRIVER,    // FR-ORD-05: Chờ tài xế
    DRIVER_ASSIGNED,       // FR-ORD-02: Đã có tài xế
    COMPLETED,             // FR-ORD-08: Hoàn thành
    CANCELLED,             // FR-ORD-07: Bị hủy
    EXPIRED                // FR-ORD-03: Hết hạn (sau 600s)
}
