package com.MHM.MultiHotelManagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Single source of truth for where uploaded images live — same property the
    // upload services write to, so serving and uploading always agree.
    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String base = "file:" + uploadDir.replace("\\", "/");
        if (!base.endsWith("/")) {
            base += "/";
        }

        registry.addResourceHandler("/hotel/**").addResourceLocations(base + "hotel/");
        registry.addResourceHandler("/room/**").addResourceLocations(base + "room/");
        registry.addResourceHandler("/food/**").addResourceLocations(base + "food/");
        registry.addResourceHandler("/owners/**").addResourceLocations(base + "owners/");
        registry.addResourceHandler("/gallery/**").addResourceLocations(base + "gallery/");
        registry.addResourceHandler("/customer/**").addResourceLocations(base + "customer/");
        registry.addResourceHandler("/checkin-id/**").addResourceLocations(base + "checkin-id/");
        registry.addResourceHandler("/location/**").addResourceLocations(base + "location/");
        registry.addResourceHandler("/images/**").addResourceLocations(base);
    }
}
