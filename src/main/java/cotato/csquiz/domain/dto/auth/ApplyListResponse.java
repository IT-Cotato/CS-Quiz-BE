package cotato.csquiz.domain.dto.auth;

import java.util.List;

public record ApplyListResponse(
        List<ApplyMemberInfo> generalList,
        List<ApplyMemberInfo> refusedList
) {
    public static ApplyListResponse from(List<ApplyMemberInfo> generalList, List<ApplyMemberInfo> refusedList) {
        return new ApplyListResponse(
                generalList,
                refusedList
        );
    }
}
