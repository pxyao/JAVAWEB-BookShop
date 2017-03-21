package goods.category.web.servlet;

import cn.itcast.servlet.BaseServlet;
import goods.category.domain.Category;
import goods.category.service.CategoryService;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class CategoryServlet
 */
@WebServlet("/CategoryServlet")
public class CategoryServlet extends BaseServlet implements Servlet {
	private CategoryService categoryService=new CategoryService();
    /**
     * @see BaseServlet#BaseServlet()
     */
    public CategoryServlet() {
        super();
    }
    
    /**
     * 查询所有分类
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	/*
    	 * 1 通过service得到所有的分类
    	 * 2 保存到request中，转发到left.jsp
    	 */
    	List<Category> parents=categoryService.findAll();
    	req.setAttribute("parents", parents);
    	return "f:/jsps/left.jsp";
    }
}
