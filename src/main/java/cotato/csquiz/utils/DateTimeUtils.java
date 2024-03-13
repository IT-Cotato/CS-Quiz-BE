package cotato.csquiz.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

    public static LocalDateTime currentServerTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
