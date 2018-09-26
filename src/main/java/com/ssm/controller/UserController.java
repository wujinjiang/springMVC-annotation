package com.ssm.controller;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.ssm.entity.User;
import com.ssm.service.UserService;


/**
 * Springmvc+Spring+Mybatis实现员工登录注册功能
 * https://www.cnblogs.com/smfx1314/p/smfx1314.html
 * 
 * 注意：如果@Controller不指定其value【@Controller】，
 * 则默认的bean名字为这个类的类名首字母小写，如果指定value【@Controller(value=”UserAction”)】
 * 或者【@Controller(“UserAction”)】，
 * 则使用value作为bean的名字。
 * @author smfx1314
 *
 */
@Controller
@Scope("prototype")
@RequestMapping("/user")
public class UserController{
 
    //注入userService
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value="/userlogin.do",method=RequestMethod.POST)
    public ModelAndView login(String username,String password,ModelAndView mv,HttpSession session) {
        User user=userService.login(username, password);
        if(user!=null){
            //登录成功，将user对象设置到HttpSession作用范围域中
            session.setAttribute("user", user);
            //转发到main请求
            /*mv.setView(new RedirectView("/smmbookapp/main"));*/
            //登录成功，跳转页面　
            mv.setView(new RedirectView("/user/getAllUser"));
        }else {
            //登录失败，设置失败信息，并调转到登录页面
            
            mv.addObject("message","登录名和密码错误，请重新输入");
            // 登录失败，跳转页面
            mv.setViewName("error");
        }
        return mv;
    }
    
    /**
     * 跳转到用户登录页面
     */
    @RequestMapping(value="/loginpage.do")
    public String loginpage() {
        
        return "login";
    }
    
    /**
     * 跳转到用户注册页面
     */
    @RequestMapping(value="/registerpage.do")
    public String registerpage() {
        
        return "register";
    }
    /**
     * 用户注册
     */
    @RequestMapping(value="/userregister.do",method=RequestMethod.POST)
    public String register(User user) {
        String username=user.getUsername();
        // 如果数据库中没有该用户，可以注册，否则跳转页面
        if (userService.findByUserName(username) == null) {
            // 添加用户
            userService.register(user);
            // 注册成功跳转到主页面
            return "redirect:/user/getAllUser";
        }else {
            // 注册失败跳转到错误页面
            return "error";
        }    
    }
    
    /**
     * 跳转到用户注册页面
     */
    @RequestMapping(value="/mainPage.do")
    public String mainPage() {
        
    	return "redirect:/user/getAllUser";
    }
    
    /**
	 * 获取所有用户列表
	 * @param request
	 * @return
	 */
	 @RequestMapping(value="/getAllUser")
	public String getAllUser(HttpServletRequest request){
		
		//BeanNameUrlHandlerMapping map = new BeanNameUrlHandlerMapping();
		//RequestMappingHandlerMapping map = new RequestMappingHandlerMapping();
		 //RequestMappingHandlerAdapter map = new RequestMappingHandlerAdapter();
		List<User> findAll = userService.findAll();
		
		request.setAttribute("userList", findAll);
		return "/allUser";
	}
	
	/**
	 * 跳转到添加用户界面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/toAddUser")
	public String toAddUser(HttpServletRequest request){
		
		return "/addUser";
	}
	
	/**
	 * 添加用户并重定向
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/addUser")
	public String addUser(User user,HttpServletRequest request){
		userService.register(user);
		return "redirect:/user/getAllUser";
	}
	
	/**
	 *编辑用户
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateUser")
	public String updateUser(User user,HttpServletRequest request){
		
		
		if(userService.update(user)){
			user = userService.findById(user.getUserid());
			request.setAttribute("user", user);
			return "redirect:/user/getAllUser";
		}else{
			return "/error";
		}
	}
	/**
	 * 根据id查询单个用户
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getUser")
	public String getUser(int id,HttpServletRequest request){
		
		request.setAttribute("user", userService.findById(id));
		return "/editUser";
	}
	/**
	 * 删除用户
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/delUser")
	public void delUser(int id,HttpServletRequest request,HttpServletResponse response){
		String result = "{\"result\":\"error\"}";
		
		if(userService.delete(id)){
			result = "{\"result\":\"success\"}";
		}
		
		response.setContentType("application/json");
		
		try {
			PrintWriter out = response.getWriter();
			out.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
}
