package collector.hotwheels.insight.util.rest;

import okhttp3.Credentials;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public final class EndPointSettings {

    private String endpoint;
    private String user;
    private String password;

    public EndPointSettings() {
        this(new EndPointSettings.Builder());
    }

    protected EndPointSettings(EndPointSettings.Builder builder) {

        this.endpoint = builder.getEndpoint();
        this.user = builder.getUser();
        this.password = builder.getPassword();
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    protected String getCredentialBase() {
        if (!isNullOrEmpty(user) && !isNullOrEmpty(password)) {
            return Credentials.basic(user, password);
        } else {
            return null;
        }
    }

    public static class Builder {

        private String endpoint;
        private String user;
        private String password;

        public Builder() {
        }

        public EndPointSettings build() {
            return new EndPointSettings(this);
        }

        protected final String getEndpoint() {
            return this.endpoint;
        }

        public EndPointSettings.Builder setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        protected final String getUser() {
            return this.user;
        }

        public EndPointSettings.Builder setUser(String user) {
            this.user = user;
            return this;
        }

        protected final String getPassword() {
            return this.password;
        }

        public EndPointSettings.Builder setPassword(String password) {
            this.password = password;
            return this;
        }
    }
}