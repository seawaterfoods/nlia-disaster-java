package tw.org.nlia.disaster.common;

import java.util.Map;

public final class Constants {

    private Constants() {}

    // 通報狀態
    public static final String STATUS_NOT_REPORTED = "X";
    public static final String STATUS_NO_LOSS = "N";
    public static final String STATUS_HAS_LOSS = "Y";

    // 帳號狀態
    public static final String ACTIVE = "Y";
    public static final String INACTIVE = "N";

    // 險種代碼
    public static final Map<String, String> INSURANCE_MAP = Map.of(
            "1", "車險",
            "2", "火險",
            "3", "水險",
            "4", "意外險",
            "5", "傷害險健康險"
    );

    // 險種簡稱
    public static final Map<String, String> INSURANCE_SHORT_MAP = Map.of(
            "車", "車險",
            "火", "火險",
            "水", "水險",
            "意", "意外險",
            "傷健", "傷害險健康險"
    );

    // 公會 CID
    public static final String NLIA_CID = "88";

    // Syslog actions
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_RESET_PWD = "RESETPWD";

    // Default pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
}
