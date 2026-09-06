package com.ams.security.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class RateLimitingFilter implements Filter {

	private static final int MAX_REQUESTS_PER_MINUTE = 100;
	private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String clientIp = req.getRemoteAddr();
		long currentTime = System.currentTimeMillis();

		requestCounts.putIfAbsent(clientIp, new RequestCounter(currentTime, 0));
		RequestCounter counter = requestCounts.get(clientIp);

		synchronized (counter) {
			if (currentTime - counter.startTime > 60000) {
				counter.startTime = currentTime;
				counter.count = 0;
			}

			counter.count++;

			if (counter.count > MAX_REQUESTS_PER_MINUTE) {
				resp.setStatus(429); // Too Many Requests
				resp.setContentType("application/json");
				resp.getWriter()
						.write("{\"status\": \"ERROR\", \"message\": \"Too many requests. Please try again later.\"}");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private static class RequestCounter {
		long startTime;
		int count;

		RequestCounter(long startTime, int count) {
			this.startTime = startTime;
			this.count = count;
		}
	}
}