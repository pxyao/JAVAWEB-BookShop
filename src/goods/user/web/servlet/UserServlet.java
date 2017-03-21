package goods.user.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goods.user.domain.User;
import goods.user.service.UserService;
import goods.user.service.exception.UserException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
    private UserService userService=new UserService();

    /**
     * 退出功能 
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.getSession().invalidate();
    	return "r:/jsps/user/login.jsp"; 
    }
    
    /**
     * 修改密码
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String updatePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/*
    	 * 1 封装表单数据到User中
    	 * 2 校验
    	 * 3从session中获取uid
    	 * 4使用uid表单中的oldpass和newpass来调用service中的方法
    	 *  >如果抛出异常 保存错误信息到request中 装法到pwd.jsp
    	 * 5保存成功信息到request中
    	 * 转发到msg.jsp
    	 */
    	User formuser=CommonUtils.toBean(request.getParameterMap(), User.class);   //把表单中的数据封装到formuser中
    	User user=(User)request.getSession().getAttribute("sessionUser");//总session 的user封装到user中 用于判断是否登录
    	//如果用户没有登录 返回到login.jsp
    	if(user==null){
    		request.setAttribute("msg", "您还没有登录");
    		return "f:/jsps/user/login.jsp";
    	}
    	/*
    	 *从session中获取Uid  从form获取老密码和新密码 
    	 */
    	try {
			userService.updatepassword(user.getUid(), formuser.getNewpass(), formuser.getLoginpass());
			request.setAttribute("msg", "修改成功");
			request.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage()); //保存错误信息到msg
			request.setAttribute("user", formuser);//回显信息
			return "f:/jsps/user/pwd.jsp";
		}
    }
    
    
    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/*
    	 * 1 封装表单数据到User
    	 * 2 校验表单数据
    	 * 3 使用service 查询 得到User
    	 * 4 查看用户是否正确
    	 *  *保存错误信息，用户名密码错误
    	 *  *保存用户数据:为了回显
    	 *  返回login.jsp
    	 * 5 如果存在 查看状态 如果状态为false
    	 *  * 保存错误信息
    	 *  * 保存表单数据
    	 *  * 转发到login.jsp
    	 * 6 登录成功：
    	 *  * 保存当前查询出的User到Session中
    	 *  * 保存当前用户的名称到cookie中 注意中文需要转码
    	 */
    	User formUser=CommonUtils.toBean(request.getParameterMap(), User.class);
    	/*
    	 * 2 校验
    	 */
    	Map<String, String> errors=validateLogin(formUser, request.getSession());
		if(errors.size()>0){
			request.setAttribute("errors", formUser);
			request.setAttribute("form", formUser);
			return "f:/jsps/user/login.jsp";
		}
		/*
		 * 3 调用UserService#login()方法
		 */
		User user=userService.login(formUser);
		/*
		 * 4 开始判断
		 */
		if(user==null){
			request.setAttribute("msg", "用户名或密码错误！");
			request.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		}else{
			if(!user.isStatus()){
				/**
				 * 
				 *  *****************此处数据库数据类型不匹配 无法查询！！ 暂时不用！！！！
				 * 
				 * 
				 */
				request.setAttribute("msg", "您还没有激活！");
				request.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			}else{
				 //保存用户到session
				request.getSession().setAttribute("sessionUser", user);
				//获取用户名保存吧到cookie中饭
				String loginname=user.getLoginname();
				loginname=URLEncoder.encode(loginname,"utf-8"); //编码
				Cookie cookie=new Cookie("loginname", loginname);
				cookie.setMaxAge(60*60*24*10);//保存10天
				response.addCookie(cookie);
				return "f:/index.jsp";//重定向到主页
			}
		}
    }

    /*
     * 登录校验方法 
     */
	private Map<String, String> validateLogin(User formUser,HttpSession session){
		Map<String, String> errors=new HashMap<>();
		
		return errors;
	}
	
    /**
     * ajax用户名是否注册校验
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    
	public String ajaxValidateLoginname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1. 获取用户名
		 */
		String loginname = request.getParameter("loginname");

		/*
		 * 2. 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateLoginname(loginname);
		/*
		 * 3. 发给客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	
	/**
	 * email是否注册校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1
		 * 获取email
		 */
		String email=request.getParameter("email");
		/*
		 * 2
		 * 通过service得到校验结果
		 */
		boolean b=userService.ajaxValidateEmail(email);

		/*
		 * 3
		 * 发送客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	/**
	 * 验证码是否正确校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1
		 * 获取输入框中的验证码
		 */
		String verifyCode=request.getParameter("verifyCode");
		/*
		 * 2
		 * 获取图片的真实验证码
		 */
		String vcode = (String) request.getSession().getAttribute("vCode");
		/*
		 * 3
		 * 进行比较 忽略大小写
		 */
		boolean b=verifyCode.equalsIgnoreCase(vcode);
		/*
		 * 4
		 * 发送客户端
		 */
		response.getWriter().print(b);
		return null;
	}
	/**
	 * 注册功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1 封装表单对象到User对象
		 */
		User formuser=CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2 校验 如果校验失败，保存错误信息，返回到regist中
		 */
		Map<String, String> errors=vaildateRegist(formuser, req.getSession());
		if(errors.size()>0){
			req.setAttribute("errors", errors);
			req.setAttribute("form", formuser);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3 使用service完成业务
		 */
		userService.regist(formuser);
		/*
		 * 4 保存成功信息 转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册成功，马上到邮箱激活");
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 激活功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1 获取参数激活码
		 * 2 用激活码调用service方法完成激活
		 *  >service 方法有可能抛出异常 把异常信息拿来 保存到request中，转发到msg.jsp
		 * 3 保存成功信息到request 转发到msg.jsp显示
		 */
		String code=req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");//通知msg.jsp显示对号
			req.setAttribute("msg", "恭喜激活成功！请马上登录！");
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");//通知msg.jsp显示错号
		}
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 注册校验
	 * @param formUser
	 * @return
	 */
	/*
	 * 对表单的字段逐个校验，如果有错使用当前字段名称为key，错误信息为value
	 * 返回map
	 */
	private Map<String, String> vaildateRegist(User formUser,HttpSession session){
		Map<String, String> errors=new HashMap<>();
		/*
		 *校验登录名 
		 */
		String loginname=formUser.getLoginname();
		if(loginname==null||loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空");
		}else if (loginname.length()<6||loginname.length()>20) {
			errors.put("loginname", "用户名长度必须在6-20之间");
		}else if(!userService.ajaxValidateLoginname(loginname)){
			errors.put("loginname", "用户名已经被注册");
		}
		/*
		 * 校验登录密码
		 */
		String loginpass=formUser.getLoginpass();
		if(loginpass==null||loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空");
		}else if (loginpass.length()<6||loginpass.length()>20) {
			errors.put("loginpass", "密码长度必须在6-20之间");
		}
		/*
		 * 校验确认密码
		 */
		String Reloginpass=formUser.getReloginpass();
		if(Reloginpass==null||Reloginpass.trim().isEmpty()){
			errors.put("reloginpass", "确认密码不能为空");
		}else if (!Reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "确认密码不一致");
		}
		/*
		 * 校验Email
		 */
		String email=formUser.getEmail();
		if(email==null||Reloginpass.trim().isEmpty()){
			errors.put("email", "Email不能为空");
		}else if (!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "Email格式错误");
		}else if(!userService.ajaxValidateEmail(email)) {
			errors.put("email", "Email已被注册！");
		}
		/*
		 * 验证码校验
		 */
		String verifyCode=formUser.getVerifyCode();
		String vcode=(String) session.getAttribute("vCode");
		if(verifyCode==null||verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空");
		}else if (!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误");
		}
		return errors;
	}
}