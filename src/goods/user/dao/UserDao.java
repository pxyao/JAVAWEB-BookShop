package goods.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.NEW;

import cn.itcast.jdbc.TxQueryRunner;
import goods.user.domain.User;

/** 
 * 用户模块持久层
 * @author Shey
 *
 */

public class UserDao {
	private QueryRunner queryRunner=new TxQueryRunner();
	
	
	/**
	 * 按Uid和loginpass查询
	 * @param uid
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public boolean findByUidAndpassword(String uid,String password) throws SQLException{
		String sql="select count(*) from t_user where uid=? and loginpass=?";
		Number number=(Number)queryRunner.query(sql, new ScalarHandler(),uid,password);
		return number.intValue()>0;
	}
	
	/**
	 * 修改密码
	 * @param password
	 * @param uid
	 * @throws SQLException
	 */
	public void updatepassword(String password,String uid) throws SQLException{
		String sql="update t_user set loginpass=? where uid=?";
		queryRunner.update(sql,password,uid);
	}
	/**
	 * 按用户名和密码查询
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws SQLException
	 */
	public User findByLoginnameAndpass(String loginname,String loginpass) throws SQLException{
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return queryRunner.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}
	
	/**
	 * 通过激活码查询用户
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public User findByCode(String code) throws SQLException{
		String sql="select * from t_user where activationCode=?";
		return queryRunner.query(sql,new BeanHandler<User>(User.class),code);
		//多行多列 用BeanHandler 单行单列则用ScalarHandler
	}
	
	/**
	 * 
	 * @param uid
	 * @param status
	 * @return
	 * @throws SQLException 
	 */
	public void updateStaatus(String uid,boolean status) throws SQLException{
		String sql="update t_user set status=? where uid=?";
		queryRunner.update(sql,status,uid);
	}
	/**
	 * 校验用户名是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateLoginname(String loginname) throws SQLException{
		String sql="select count(1) from t_user where loginname=?";
		Number number=(Number)queryRunner.query(sql, new ScalarHandler(),loginname);
		return number.intValue()==0;
	}
	
	/**
	 * 校验Email是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateEmail(String email) throws SQLException{
		String sql="select count(1) from t_user where email=?";
		Number number=(Number)queryRunner.query(sql, new ScalarHandler(),email);
		return number.intValue()==0;
	}
	/**
	 * 添加用户
	 * @throws SQLException 
	 */
	public void addUser(User user) throws SQLException{
		String sql="insert into t_user values(?,?,?,?,?,?)";
		Object[] parmas={user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),user.isStatus(),user.getActivationCode()};
		queryRunner.update(sql,parmas);
	}
	
}
