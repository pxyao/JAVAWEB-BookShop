$(function() {
	/*
	 * 1. 得到所有的错误信息，循环遍历。调用一个方法来确定是否显示错误信息
	 */
	$(".errorClass").each(function() {
		showError($(this));
	});
	/*
	 * 2. 切换注册按钮的图片
	 */
	$("#submitBtn").hover(function() {
		$("#submitBtn").attr("src", "/goods/images/regist2.jpg");
	}, function() {
		$("#submitBtn").attr("src", "/goods/images/regist1.jpg");
	});
	/*
	 * 3. 输入框得到焦点隐藏
	 */
	$(".InputClass").focus(function() {
		var labelId = $(this).attr("id") + "Error";
		$("#" + labelId).text("");// 把label中的内容清空
		showError($("#" + labelId));
	});
	/*
	 * 4. 输入框失去焦点校验
	 */
	$(".InputClass").blur(
			function() {
				var id = $(this).attr("id");
				var funname = "validate" + id.substring(0, 1).toUpperCase()
						+ id.substring(1) + "()";
				eval(funname)
			});
	$("#registform").submit(function() {
		var bool = true;
		if (!validateLoginname()) {
			bool = false;
		}
		if (!validateLoginpass()) {
			bool = false;
		}
		if (!validateReloginpass()) {
			bool = false;
		}
		if (!validateEmail()) {
			bool = false;
		}
		if (!validateVerifyCode()) {
			bool = false;
		}
		return bool;
	});
});

/**
 * 登录名校验方法
 * 
 * @returns
 */
function validateLoginname() {
	var id = "loginname";
	var value = $("#" + id).val();
	/*
	 * 1 非空校验
	 */
	if (!value) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("用户名不能为为空");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 2 长度校验
	 */
	if (value.length < 6 || value.length > 20) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("用户名长度必须在6-20之间");
		showError($("#" + id + "Error"));
		return false;
	}

	/*
	 * 3.是否注册校验
	 */
	$.ajax({
		url:"/goods/UserServlet",//要请求的servlet
		data:{method:"ajaxValidateLoginname", loginname:value},//给服务器的参数
		type:"POST",
		dataType:"json",
		async:false,//是否异步请求，如果是异步，那么不会等服务器返回，我们这个函数就向下运行了。
		cache:false,
		success:function(result) {
			if(!result) {//如果校验失败
				$("#" + id + "Error").text("用户名已被注册！");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});
	return true;
}

/**
 * 密码校验
 * 
 * @returns
 */
function validateLoginpass() {
	var id = "loginpass";
	var value = $("#" + id).val();
	/*
	 * 1 非空校验
	 */
	if (!value) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("密码不能为为空");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 2 长度校验
	 */
	if (value.length < 5 || value.length > 20) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("密码长度必须在6-20之间");
		showError($("#" + id + "Error"));
		return false;
	}
	return true;
}

/**
 * 验证密码校验
 * 
 * @returns
 */
function validateReloginpass() {
	var id = "reloginpass";
	var value = $("#" + id).val();
	/*
	 * 1 非空校验
	 */
	if (!value) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("确认密码不能为为空");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 2 和密码是否一样校验
	 */
	if (value != $("#loginpass").val()) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("必须和密码一致");
		showError($("#" + id + "Error"));
		return false;
	}
	return true;
}

/**
 * 邮箱校验
 * 
 * @returns
 */
function validateEmail() {
	var id = "email";
	var value = $("#" + id).val();
	/*
	 * 1 非空校验
	 */
	if (!value) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("Email不能为空");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 2 和密码是否一样校验
	 */
	if (!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/
			.test(value)) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("邮箱格式不正确");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 3 邮箱是否注册校验 
	 */
	$.ajax({
		url:"/goods/UserServlet",//要请求的servlet
		data:{method:"ajaxValidateEmail", email:value},//给服务器的参数
		type:"POST",
		dataType:"json",
		async:false,//是否异步请求，如果是异步，那么不会等服务器返回，我们这个函数就向下运行了。
		cache:false,
		success:function(result) {
			if(!result) {//如果校验失败
				$("#" + id + "Error").text("邮箱已经注册！");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});
	return true;
}
/**
 * 验证码是否正确校验
 * 
 * @returns
 */
function validateVerifyCode() {
	var id = "verifyCode";
	var value = $("#" + id).val();
	/*
	 * 1 非空校验
	 */
	if (!value) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("验证码不能为空");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 2 验证码长度校验
	 */

	if (value.length != 4) {
		/*
		 * 获取对应的label 添加错误信息 显示label
		 */
		$("#" + id + "Error").text("验证码错误");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 * 3 验证码是否正确校验
	 */
	$.ajax({
		url:"/goods/UserServlet",//要请求的servlet
		data:{method:"ajaxValidateVerifyCode", verifyCode:value},//给服务器的参数
		type:"POST",
		dataType:"json",
		async:false,//是否异步请求，如果是异步，那么不会等服务器返回，我们这个函数就向下运行了。
		cache:false,
		success:function(result) {
			if(!result) {//如果校验失败
				$("#" + id + "Error").text("验证码错误！");
				showError($("#" + id + "Error"));
				return false;
			}
		}
	});

	return true;
}

/*
 * 判断当前元素是否存在内容，如果存在显示，不存在不显示
 */
function showError(ele) {
	var text = ele.text();// 获取元素的内容
	if (!text) {// 如果没有内容
		ele.css("display", "none");// 隐藏元素
	} else {// 如果有内容
		ele.css("display", "");// 显示元素
	}
}
function refresh() {
	/*
	 * 换一张验证码 获取<img>元素 重新设置它的src 使用毫秒来添加参数
	 */
	$("#imgVerifyCode").attr("src",
			"/goods/VerifyCodeServlet?a=" + new Date().getTime());
}