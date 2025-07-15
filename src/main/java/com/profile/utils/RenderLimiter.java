package com.profile.utils;

import java.util.concurrent.Semaphore;

public class RenderLimiter {
    public static final Semaphore SEMAPHORE = new Semaphore(2);
}