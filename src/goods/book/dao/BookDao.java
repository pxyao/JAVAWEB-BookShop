package goods.book.dao;

import org.apache.commons.dbutils.QueryRunner;

import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	QueryRunner queryRunner=new TxQueryRunner();
	
	
}
