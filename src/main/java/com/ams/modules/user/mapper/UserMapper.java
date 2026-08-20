package com.ams.modules.user.mapper;

import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.dto.UserSummaryResponse;
import com.ams.modules.user.entity.User;

public class UserMapper {

	public static UserResponse toUserResponse(User user) {
		if (user == null)
			return null;
		return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getFullName(),
				user.getStatus(), user.getCreatedAt());
	}

	public static UserSummaryResponse toUserSummaryResponse(User user) {
		if (user == null)
			return null;
		return new UserSummaryResponse(user.getUserId(), user.getUsername(), user.getEmail());
	}
}