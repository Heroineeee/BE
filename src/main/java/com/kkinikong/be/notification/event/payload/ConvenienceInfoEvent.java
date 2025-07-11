package com.kkinikong.be.notification.event.payload;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.user.domain.User;

public record ConvenienceInfoEvent(User sender, ConveniencePost post) {}
