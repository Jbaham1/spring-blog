package com.codeup.springblog.controllers;

import com.codeup.springblog.models.Post;
import com.codeup.springblog.models.User;
import com.codeup.springblog.repo.PostRepo;
import com.codeup.springblog.repo.UserRepo;
import com.codeup.springblog.services.EmailService;
import com.codeup.springblog.services.UserDetailsLoader;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String viewPostForm(Model vModel){
        vModel.addAttribute("post",new Post());
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute Post postToSave){

        User userToAdd = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // set the user
        postToSave.setOwner(userToAdd);

        // Now lets save our post;
        postDao.save(postToSave);

        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/update")
    public String updatePostForm(@PathVariable Long id, Model model){

        Post postFromDb = postDao.getOne(id);

        model.addAttribute("oldPost",postFromDb);

        return "posts/update";
    }

    @PostMapping("/posts/{id}/update")
    @ResponseBody
    public String updatePost(@PathVariable Long id,@RequestParam("post_title") String title, @RequestParam("post_body") String body){

        Post postToSave = new Post(id,title,body);

        postDao.save(postToSave);
        return "You updated an post.";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id){
        postDao.deleteById(id);
        return "posts/index";

    }
}

