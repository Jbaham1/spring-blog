package com.codeup.springblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class RollDiceController {
    @GetMapping("/roll-dice/{guess}")
    public String rollDice(@PathVariable int guess, Model viewModel ){
        int randomNum = ThreadLocalRandom.current().nextInt(1,6+1);
        viewModel.addAttribute("isCorrect", randomNum == guess);
        viewModel.addAttribute("guess", guess);
        viewModel.addAttribute("randomNum", randomNum);
        return "roll-dice";
    }
}
