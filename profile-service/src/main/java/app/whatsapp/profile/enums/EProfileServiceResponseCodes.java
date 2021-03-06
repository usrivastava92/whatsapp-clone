package app.whatsapp.profile.enums;

import app.whatsapp.common.models.IResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EProfileServiceResponseCodes implements IResponseCode {

    USER_NOT_FOUND("30", "F", "User does not exist"),
    INVALID_CREDENTIALS("403", "F", "Invalid Credentials"),
    INVALID_JWT("50", "F", "Invalid JWT"),
    INVALID_TOKEN("60", "F", "Invalid token");

    private final String code;
    private final String status;
    private final String message;

}
