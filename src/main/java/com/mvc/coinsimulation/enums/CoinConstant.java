package com.mvc.coinsimulation.enums;

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
    ASSET_RESET_THRESHOLD(10000000),

    /**
     * 사용자의 초기 자산 값을 나타냅니다.
     * 이 값은 사용자의 자산을 초기화하는 데 사용됩니다.
     */
    INITIAL_ASSET_VALUE(30000000);
    private final int value;

    CoinConstant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
