package com.fitness.activityservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
@Slf4j
public class ActivityController {
	
	private ActivityService activityService;
	
	@PostMapping
	public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request, @RequestHeader("X-User-ID") String userId){
		request.setUserId(userId);
		log.info("ActivityController:: Received activity tracking request for user: " + request.getUserId());
		return ResponseEntity.ok(activityService.trackActivity(request));
	}
	
	@GetMapping
	public ResponseEntity<List<ActivityResponse>> trackActivity(@RequestHeader("X-User-ID") String userId){
		return ResponseEntity.ok(activityService.getUserActivities(userId));
	}
}
