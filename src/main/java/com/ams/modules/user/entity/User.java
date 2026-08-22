package com.ams.modules.user.entity;

public class User {
    private Long id;
    private String username;
    private String password;
    private String status;
    private Integer is2faEnabled;
    private String twoFactorSecret;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getIs2faEnabled() { return is2faEnabled; }
    public void setIs2faEnabled(Integer is2faEnabled) { this.is2faEnabled = is2faEnabled; }

    public String getTwoFactorSecret() { return twoFactorSecret; }
    public void setTwoFactorSecret(String twoFactorSecret) { this.twoFactorSecret = twoFactorSecret; }
}