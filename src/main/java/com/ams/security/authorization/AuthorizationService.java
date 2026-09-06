package com.ams.security.authorization;

import java.util.Set;

import com.ams.security.crypto.JwtTokenProvider;
import com.ams.security.rbac.RBACManager;

public class AuthorizationService {

	private final RBACManager rbacManager;

	public AuthorizationService() {
		this.rbacManager = new RBACManager();
	}

	public AuthorizationService(RBACManager rbacManager) {
		this.rbacManager = rbacManager;
	}

	/**
	 * JWT টোকেন ভ্যালিডেশন চেক করে
	 */
	public boolean validateToken(String token) {
		if (token == null || token.isEmpty()) {
			return false;
		}

		// "Bearer " প্রিফিক্স থাকলে তা ট্রিম করা
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		return JwtTokenProvider.validateToken(token);
	}

	/**
	 * নির্দিষ্ট ইউজারের প্রয়োজনীয় পারমিশন আছে কিনা চেক করে
	 */
	public boolean hasPermission(Long userId, String requiredPermission) {
		if (userId == null || requiredPermission == null || requiredPermission.isEmpty()) {
			return false;
		}
		return rbacManager.hasPermission(userId, requiredPermission);
	}

	/**
	 * নির্দিষ্ট ইউজারের প্রয়োজনীয় রোল আছে কিনা চেক করে
	 */
	public boolean hasRole(Long userId, String requiredRole) {
		if (userId == null || requiredRole == null || requiredRole.isEmpty()) {
			return false;
		}
		return rbacManager.hasRole(userId, requiredRole);
	}

	/**
	 * ইউজারের সমস্ত পারমিশন রিটার্ন করে
	 */
	public Set<String> getUserPermissions(Long userId) {
		return rbacManager.getUserPermissions(userId);
	}

	/**
	 * ইউজারের সমস্ত রোল রিটার্ন করে
	 */
	public Set<String> getUserRoles(Long userId) {
		return rbacManager.getUserRoles(userId);
	}

	/**
	 * একাধিক পারমিশনের যেকোনো একটি (ANY) থাকলেও এক্সেস গ্রান্ট করবে
	 */
	public boolean hasAnyPermission(Long userId, String... permissions) {
		if (userId == null || permissions == null) {
			return false;
		}
		Set<String> userPermissions = rbacManager.getUserPermissions(userId);
		for (String permission : permissions) {
			if (userPermissions.contains(permission)) {
				return true;
			}
		}
		return false;
	}
}