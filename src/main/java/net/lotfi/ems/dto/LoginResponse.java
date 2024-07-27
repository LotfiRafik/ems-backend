package net.lotfi.ems.dto;

public class LoginResponse {
    private String token;
    private long expiresIn;
    private Long id;
    // TODO include other user info..

    // *********************  Getters *********************
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;

    }

    public long getExpiresIn() {
        return expiresIn;
    }

    //   ********************* setters  *********************
    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
