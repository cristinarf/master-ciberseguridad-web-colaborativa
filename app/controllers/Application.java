package controllers;

import models.Constants;
import models.User;
import play.i18n.Messages;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    private static void checkTeacher(){
        checkUser();

        User u = (User) renderArgs.get("user");
        if (!u.getType().equals(Constants.User.TEACHER)){
            return;
        }
    }

    private static void checkUser(){
        if (session.contains("username")){
            User u = User.loadUser(session.get("username"));
            if (u != null){
                renderArgs.put("user", u);
                return;
            }
        }
        Secure.login();
    }

    public static void index() {
        checkUser();

        User u = (User) renderArgs.get("user");

        if (u.getType().equals(Constants.User.TEACHER)){
            List<User> students = User.loadStudents();
            render("Application/teacher.html", u, students);
        }else{
            render("Application/student.html", u);
        }
    }


    public static void removeStudent(String student) {
        ensureUserIsLoggedIn();
        ensureUserIsATeacher();

        checkTeacher();

        User.remove(student);
        index();
    }


    public static void setMark(String student) {
        ensureUserIsLoggedIn();
        ensureUserIsATeacher();

        User u = User.loadUser(student);
        render(u);
    }

    public static void doSetMark(String student, Integer mark) {
        ensureUserIsLoggedIn();
        ensureUserIsATeacher();

        User u = User.loadUser(student);
        u.setMark(mark);
        u.save();
        index();
    }

    private static void ensureUserIsLoggedIn() {
        if (session.contains("username")) {
            return;
        }

        flash.put("error", Messages.get("Public.authentication.user_not_logged_in"));
        Secure.login();
    }

    private static void ensureUserIsATeacher() {
        if (session.contains("role") && session.get("role").equals(Constants.User.TEACHER)) {
            return;
        }

        flash.put("error", Messages.get("Public.authorization.user_is_not_a_teacher"));
        Application.index();
    }
}