package com.looklog.controller;

import com.looklog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

  private final TagRepository tagRepository;

  @ModelAttribute
  public void addTagsToModel(Model model) {
    model.addAttribute("styleTags", tagRepository.findByType("style"));
    model.addAttribute("seasonTags", tagRepository.findByType("season"));
    model.addAttribute("colorTags", tagRepository.findByType("color"));
  }
}