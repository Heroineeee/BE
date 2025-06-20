package com.kkinikong.be.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.feedback.domain.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}
