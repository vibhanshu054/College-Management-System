package com.dashboard.service;

import java.util.Map;

public interface ProfileService {
    Map<String, Object> getMyProfile(String username, String role, String universityId);
}