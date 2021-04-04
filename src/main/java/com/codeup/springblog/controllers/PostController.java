package com.codeup.springblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PostController {
    @GetMapping("/posts")
    @ResponseBody
    public String allPosts() {
        return "This is index";
    }

    @GetMapping("/posts/{id}")
    @ResponseBody
    public String singlePost(@PathVariable Long id) {
        return "This is a single post";
    }
    @GetMapping("/posts/create")
    @ResponseBody
    public String createPost(){
        return "This is the creation";
    }
    @PostMapping("/posts/create")
    @ResponseBody
    public String CreatePost(){
        return "You created a post";
    }

}

