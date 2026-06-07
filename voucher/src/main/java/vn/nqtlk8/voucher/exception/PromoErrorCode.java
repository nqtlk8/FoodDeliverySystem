package vn.nqtlk8.voucher.exception;

public enum PromoErrorCode {
    ERR_PROMO_EXPIRED("Voucher đã hết hạn hoặc không tồn tại"),
    ERR_PROMO_MIN_SPEND("Chưa đạt mức chi tiêu tối thiểu"),
    ERR_PROMO_FULL("Voucher đã hết lượt sử dụng");

    private final String message;

    PromoErrorCode(String message) { this.message = message; }
    public String getMessage() { return message; }
}
