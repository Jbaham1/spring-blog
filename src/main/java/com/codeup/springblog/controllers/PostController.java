package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.repo.PostRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class PostController {
    //    List<Post> posts = new ArrayList<>();
    private final PostRepo postDao;

    public PostController(PostRepo postDao) {
        this.postDao = postDao;
    }

    @GetMapping("/posts")
    public String allPosts(Model viewModel) {
        List<Post> postFromDB = postDao.findAll();
        viewModel.addAttribute("posts", postFromDB);
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String singlePost(@PathVariable Long id, Model viewModel) {
        viewModel.addAttribute("post", postDao.getOne(id));
        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String createPost(Model viewModel) {
        viewModel.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String CreatePost(@ModelAttribute Post postToSave) {
        postDao.save(postToSave);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String viewEditForm(Model viewModel, @PathVariable Long id) {
        viewModel.addAttribute("post", postDao.getOne(id));
        return "posts/create";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(@ModelAttribute Post postToUpdate, @PathVariable Long id) {

        postToUpdate.setId(id);

        postDao.save(postToUpdate);

        return "redirect:/posts";
    }
}

