package com.MHM.MultiHotelManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/hotel/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/hotel/");

        registry.addResourceHandler("/room/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/room/");

        registry.addResourceHandler("/food/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/food/");

        registry.addResourceHandler("/owners/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/owners/");

        registry.addResourceHandler("/gallery/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/gallery/");

        registry.addResourceHandler("/customer/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/customer/");

        registry.addResourceHandler("/checkin-id/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/checkin-id/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:F:/GitHub/SPRING/assets/");
    }
}
