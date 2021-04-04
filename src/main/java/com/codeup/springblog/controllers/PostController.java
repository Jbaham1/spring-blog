package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
public class PostController {
//    List<Post> posts = new ArrayList<>();

    @GetMapping("/posts")
    public String allPosts() {

        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String singlePost(@PathVariable Long id) {

        return "posts/show";
    }

    @GetMapping("/posts/create")
    @ResponseBody
    public String createPost() {
        return "This is the creation";
    }

    @PostMapping("/posts/create")
    @ResponseBody
    public String CreatePost() {
        return "You created a post";
    }

}

