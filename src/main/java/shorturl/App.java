package shorturl;

import blade.kit.HashidKit;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Sql2oPlugin;

import com.blade.Blade;
import com.blade.Bootstrap;
import com.blade.route.RouteHandler;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

public class App extends Bootstrap {

	private static final HashidKit HASHIDS = new HashidKit("blade-shorturl");
	
	private Model<UrlModel> urlModel;
	
	@Override
	public void init(Blade blade) {
		
		blade.get("/", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				response.render("index");
			}
		});
		
		blade.get("/:key", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				String key = request.param("key").replaceAll("[^A-Za-z0-9]", "");
				long[] numbers = HASHIDS.decode(key);

				if (null == numbers || numbers.length < 1) {
					response.text("没有找到");
					return;
				}
				int id = (int) numbers[0];
				String result = urlModel.fetchByPk(id).getUrl();
				if (result == null) {
					response.text("没有找到");
					return;
				}
				response.redirect(result);
			}
		});
		
		blade.post("/", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				String resJsp = "index";
				
				String longUrl = request.query("url");
				
				if (!URLKit.isURL(longUrl)) {
					request.attribute("error", "无效的URL");
					response.render(resJsp);
					return;
				}
				
				Integer id = urlModel.insert().param("url", longUrl).executeAndCommit();
				if (id == 0) {
					request.attribute("error", "保存失败");
					response.render(resJsp);
					return;
				}
				
				String hash = HASHIDS.encode(id);
				request.attribute("url_hash", hash);
				System.out.println("id = " + id + ",url_hash=" + hash);
				response.render(resJsp);
			}
		});
		
		blade.staticFolder("/static/");
		
		Sql2oPlugin sql2oPlugin = blade.plugin(Sql2oPlugin.class);
		sql2oPlugin.config("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/short_url", "root", "root").run();
		
	}
	
	@Override
	public void contextInitialized(Blade blade) {
		urlModel = new Model<UrlModel>(UrlModel.class);
	}
}