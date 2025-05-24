package com.kkinikong.be.convenience.dto.response;

public record ConveniencePostInfoResponse(
    long correctCount, long incorrectCount, Boolean userSelection) {}
