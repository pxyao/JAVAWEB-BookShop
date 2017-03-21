package goods.book.web.servlet;

import cn.itcast.servlet.BaseServlet;
import goods.book.service.BookService;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class BookServlet
 */
@WebServlet("/BookServlet")
public class BookServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    private BookService bookService=new BookService();
    /**
     * @see BaseServlet#BaseServlet()
     */
    public BookServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

}
