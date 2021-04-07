package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repo.PostRepo;
import com.codeup.springblog.repo.UserRepo;
import com.codeup.springblog.services.EmailService;
import com.codeup.springblog.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class PostController {
    @Autowired
    private EmailService emailService;

    private final PostRepo postDao;
    private final UserRepo userDao;

    PostController(PostRepo postDao, UserRepo userDao){
        this.postDao = postDao;
        this.userDao = userDao;
    }


    @GetMapping("/posts")
    public String seeAllPosts(Model viewModel){
        List<Post> postsFromDb = postDao.findAll();
        viewModel.addAttribute("posts", postsFromDb);
        // do not use a / to reference a template
        return "posts/index";
    }
    @GetMapping("/posts/{id}")
    public String showOnePost(@PathVariable Long id, Model vModel){
        vModel.addAttribute("post", postDao.getOne(id));
        return "post/show";
    }
    @GetMapping("/posts/create")
    public String viewPostForm(Model model){
        model.addAttribute("post", new Post());
        return "posts/create";
    }
    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute Post postToBeSaved){
        User userToAdd = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // set the user
        postToBeSaved.setOwner(userToAdd);
        // Now lets save our post;
        postDao.save(postToBeSaved);
        emailService.prepareAndSend(postToBeSaved, "New Post!", "A new Post has been created in the app");
        return "redirect:/posts";
    }
    @GetMapping("/posts/{id}/update")
    public String updatePostForm(@PathVariable Long id, Model model){
        Post postFromDb = postDao.getOne(id);
        model.addAttribute("oldPost",postFromDb);
        return "posts/update";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post postToUpdate){

        User userToAdd = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postToUpdate.setId(id);

        // set the user
        postToUpdate.setOwner(userToAdd);

        postDao.save(postToUpdate);

        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/delete")
    @ResponseBody
    public String deletePost(@PathVariable Long id){
        postDao.deleteById(id);
        return "You deleted an post.";
    }
}

