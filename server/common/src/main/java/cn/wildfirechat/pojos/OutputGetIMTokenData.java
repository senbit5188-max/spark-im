package cn.wildfirechat.pojos;

public class OutputGetIMTokenData {
    private String userId;
    private String token;
    private String serverLabel;

    public OutputGetIMTokenData() {
    }

    public OutputGetIMTokenData(String userId, String imToken, String label) {
        this.userId = userId;
        this.token = imToken;
        this.serverLabel = label;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerLabel() {
        return serverLabel;
    }

    public void setServerLabel(String serverLabel) {
        this.serverLabel = serverLabel;
    }
}
