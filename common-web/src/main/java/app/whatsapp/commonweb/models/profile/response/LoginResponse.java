package app.whatsapp.commonweb.models.profile.response;

import app.whatsapp.common.models.BaseResponse;
import app.whatsapp.common.models.IResponseCode;
import app.whatsapp.commonweb.models.profile.UserProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse extends BaseResponse {

    private String token;
    private UserProfile userProfile;

}
