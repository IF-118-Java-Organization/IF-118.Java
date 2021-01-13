package com.softserve.itacademy.controller.view;

import com.softserve.itacademy.security.perms.roles.AdminRolePermission;
import com.softserve.itacademy.security.perms.roles.UserRolePermission;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping
public class ViewController {

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String homeView() {
        return "home.html";
    }

    @GetMapping(path = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginView(Principal principal) {
        if (principal == null) {
            return "login.html";
        } else {
            return "redirect:user";
        }
    }

    @GetMapping(path = "/registration", produces = MediaType.TEXT_HTML_VALUE)
    public String registrationView(Principal principal) {
        if (principal == null) {
            return "registration.html";
        } else {
            return "redirect:user";
        }
    }

    @AdminRolePermission
    @GetMapping(path = "/admin", produces = MediaType.TEXT_HTML_VALUE)
    public String adminView() {
        return "admin-panel.html";
    }

//    @UserRolePermission
    @GetMapping(path = "/user", produces = MediaType.TEXT_HTML_VALUE)
    public String userView() {
        return "user-panel.html";
    }

    @GetMapping(path = "/profile", produces = MediaType.TEXT_HTML_VALUE)
    public String profileView() {
        return "profile.html";
    }

    @GetMapping(path = "/group", produces = MediaType.TEXT_HTML_VALUE)
    public String groupView() {
        return "group.html";
    }

    @GetMapping(path = "/course", produces = MediaType.TEXT_HTML_VALUE)
    public String courseView() {
        return "course.html";
    }

    @GetMapping(path = "/invite", produces = MediaType.TEXT_HTML_VALUE)
    public String inviteView() {
        return "invitation.html";
    }

}
