package goods.category.service;


import java.sql.SQLException;
import java.util.List;

import goods.category.dao.CategoryDao;
import goods.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao=new CategoryDao();
	
	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll(){
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
