package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repo.PostRepo;
import com.codeup.springblog.repo.UserRepo;
import com.codeup.springblog.services.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class PostController {
    //    List<Post> posts = new ArrayList<>();
    private final PostRepo postDao;
    private final UserRepo userDao;
    private final EmailService emailService;

    public PostController(PostRepo postDao, UserRepo userDao, EmailService emailService) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.emailService = emailService;
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
        viewModel.addAttribute("user", userDao.getOne(id));
        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String createPost(Model viewModel) {
        viewModel.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String CreatePost(@ModelAttribute Post post) {
        User user = userDao.getOne(1L);
        post.setOwner(user);
        Post savedPost = postDao.save(post);
        emailService.prepareAndSend(savedPost, "New Ad!", "A new Ad has been created in the app");
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String viewEditForm(Model viewModel, @PathVariable Long id) {
        viewModel.addAttribute("post", postDao.getOne(id));
        return "posts/create";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(@ModelAttribute Post postToUpdate, @ModelAttribute User userToAdd, @PathVariable Long id) {

        postToUpdate.setId(id);

        postToUpdate.setOwner(userToAdd);

        postDao.save(postToUpdate);

        return "redirect:/posts";
    }
}

