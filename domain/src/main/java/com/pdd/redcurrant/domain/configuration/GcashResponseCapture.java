package com.pdd.redcurrant.domain.configuration;

import org.springframework.stereotype.Component;

@Component
public class GcashResponseCapture {

    private final ThreadLocal<String> holder = new ThreadLocal<>();

    public void set(String json) {
        holder.set(json);
    }

    public String get() {
        return holder.get();
    }

    public void clear() {
        holder.remove();
    }

}
