package com.MHM.MultiHotelManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/hotel/**")
                .addResourceLocations("file:uploads/hotel/");

        registry.addResourceHandler("/room/**")
                .addResourceLocations("file:uploads/room/");

        registry.addResourceHandler("/food/**")
                .addResourceLocations("file:uploads/food/");

        registry.addResourceHandler("/owners/**")
                .addResourceLocations("file:uploads/owners/");

        registry.addResourceHandler("/gallery/**")
                .addResourceLocations("file:uploads/gallery/");

        registry.addResourceHandler("/customer/**")
                .addResourceLocations("file:uploads/customer/");

        registry.addResourceHandler("/checkin-id/**")
                .addResourceLocations("file:uploads/checkin-id/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }
}
