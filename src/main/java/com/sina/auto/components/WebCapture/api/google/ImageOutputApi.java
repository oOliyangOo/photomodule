package com.sina.auto.components.WebCapture.api.google;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiParam;

//@Api(value = "ImageOutputApi", description = "the PageSnapshot API")
@RequestMapping(value = "/front/api/google/ImageOutputApi", method = RequestMethod.GET)
public interface ImageOutputApi {
    public void getPicture(//byte[]
    		@ApiParam(value = "url", required = true) @RequestParam(required = true) String url,@ApiParam(value = "appid", required = true) @RequestParam(required = true) String appid
    );
}
