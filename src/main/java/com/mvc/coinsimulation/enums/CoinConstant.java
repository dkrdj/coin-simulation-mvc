package com.mvc.coinsimulation.enums;

import java.math.BigDecimal;

/**
 * 서비스에서 사용되는 상수들 모음
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
public enum CoinConstant {
    /**
     * 자산 재설정을 위한 임계값을 나타냅니다.
     * 사용자의 자산이 이 임계값 아래로 떨어지면 자산이 재설정됩니다.
     */
    ASSET_RESET_THRESHOLD(new BigDecimal(10000000)),

    /**
     * 사용자의 초기 자산 값을 나타냅니다.
     * 이 값은 사용자의 자산을 초기화하는 데 사용됩니다.
     */
    INITIAL_ASSET_VALUE(new BigDecimal(30000000)),
    COIN_MIN_VALUE(new BigDecimal(1000));
    private final BigDecimal value;

    CoinConstant(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

}
