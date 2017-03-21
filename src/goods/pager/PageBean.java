package goods.pager;

import java.util.List;
/**
 * 分页Bean 它会在各层传递
 * @author Shey
 *
 * @param <T>
 */
public class PageBean<T> {
	private int pc;//当前页码
	private int tr;//总记录数
	private int ps;//每页记录数
	private String url;//请求路径参数，例如 /BookServlet?method=findXXX&cid=1&bname=2
	private List<T> beanlsit;
	
	public int getTp(){ //计算总页数
		int tp=tr/ps;
		return tr%ps==0 ? tp:tp+1;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<T> getBeanlsit() {
		return beanlsit;
	}
	public void setBeanlsit(List<T> beanlsit) {
		this.beanlsit = beanlsit;
	}
}
