package goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.management.RuntimeErrorException;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import goods.user.dao.UserDao;
import goods.user.domain.User;
import goods.user.service.exception.UserException;

/**
 * 用户模块业务层
 * @author Shey
 *
 */
public class UserService {
	private UserDao userDao=new UserDao();
	
	public void updatepassword(String uid,String newpass,String oldpass) throws UserException{
		try {
			/*
			 * 1 校验老密码
			 */
			boolean b=userDao.findByUidAndpassword(uid, oldpass);
			/*
			 * 2 修改密码
			 */
			if(!b){
				/*
				 * 密码错误
				 */
				throw new UserException("老密码错误！");
			}
			/*
			 *  3 修改密码
			 */
			userDao.updatepassword(newpass, uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 登录功能
	 * @param user
	 * @return
	 */
	public User login(User user){
		try { 
			return userDao.findByLoginnameAndpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	/**
	 * 激活功能
	 * @param code 激活码
	 * @throws UserException 
	 * @throws SQLException 
	 */
	public void activation(String code) throws UserException{
		/*
		 * 1 通过激活码查询用户
		 * 2 如果User为null，说明是无效的激活码。抛出异常 给出异常信息(无效激活码)
		 * 3 查看用户状态是否为true 如果true 抛出异常 给出异常信息(请不要二次激活)
		 * 4 修改用户状态为true
		 */
		try {
		User user = userDao.findByCode(code);
		if(user==null){ 
			throw new UserException("无效的激活码");
		}
		if(user.isStatus()){
			throw new UserException("您已经激活过了，不要二次激活");
		}
		userDao.updateStaatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 校验用户名是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 校验Email是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateEmail(String email){
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	public void regist(User user){
		/*
		 * 1 数据的补齐
		 */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*
		 * 2 向数据库插入
		 */
		try {
			userDao.addUser(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*
		 * 3 发邮件
		 */
		/*
		 * 把配置和文件内容加载到prop中
		 */
		Properties properties=new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		/*
		 * 登录邮件服务器，得到Session
		 */
		String host=properties.getProperty("host");//服务器主机名
		String name=properties.getProperty("username");//登录名
		String pass=properties.getProperty("password");//登录密码
		Session session=MailUtils.createSession(host, name, pass);
		/*
		 * 创建Mail对象
		 */
		String from=properties.getProperty("from");
		String to=user.getEmail();
		String subject=properties.getProperty("subject");
		//MessageForm.format方法会把第一个参数中的{0} 使用第二个参数来替换
		String content=MessageFormat.format(properties.getProperty("content"), user.getActivationCode());
		Mail mail=new Mail(from,to,subject,content);
		/*
		 * 发送邮件
		 */
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
