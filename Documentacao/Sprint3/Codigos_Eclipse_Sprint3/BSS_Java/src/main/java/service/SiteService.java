package service;

import java.io.IOException;
import dao.SiteDAO;
import model.Site;
import spark.Request;
import spark.Response;


public class SiteService {
	private SiteDAO siteDAO;
	
	
	public SiteService() {
		try {
			siteDAO = new SiteDAO();
			siteDAO.conectar();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void closeDB() {
		siteDAO.closeDB();
	}

	public Object add(Request request, Response response) {
		String url = request.queryParams("url");
		String nome = request.queryParams("nome"); 
		Site site = new Site(url, nome);

		siteDAO.add(site);

		response.status(201); // 201 Created
		return url;
	}

	public Object get(Request request, Response response) {
		String url = request.params(":url");
		
		Site site = (Site) siteDAO.getSite(url);
		
		if (site != null) {
    	    response.header("Content-Type", "application/xml");
    	    response.header("Content-Encoding", "UTF-8");

            return "<site>\n" + 
            		"\t<nome>" + site.getNome() + "</nome>\n" +
            		"\t<url>" + site.getUrl() + "</url>\n" +
            		"</site>\n";
        } else {
            response.status(404); // 404 Not found
            return "Site " + url + " nao encontrado.";
        }
	}

	public Object update(Request request, Response response) {
        String url = request.params(":url");
        
		Site site = (Site) siteDAO.getSite(url);

        if (site != null) {
        	site.setUrl(request.queryParams("url"));
        	site.setNome(request.queryParams("nome"));

        	siteDAO.atualizarSite(site);
        	
            return url;
        } else {
            response.status(404); // 404 Not found
            return "Site nao encontrado.";
        }

	}

	public Object remove(Request request, Response response) {
		String url = request.params(":url");

        Site site = (Site) siteDAO.getSite(url);

        if (site != null) {

            siteDAO.excluirSite(url);

            response.status(200); // success
        	return url;
        } else {
            response.status(404); // 404 Not found
            return "Site nao encontrado.";
        }
	}

	public Object getAll(Request request, Response response) {
		StringBuffer returnValue = new StringBuffer("<sites type=\"array\">");
		for (Site site : siteDAO.getSites()) {
			returnValue.append("\n<site>\n" + 
            		"\t<url>" + site.getUrl() + "</url>\n" +
            		"\t<nome>" + site.getNome() + "</nome>\n" +
            		"</site>\n");
		}
		returnValue.append("</sites>");
	    response.header("Content-Type", "application/xml");
	    response.header("Content-Encoding", "UTF-8");
		return returnValue.toString();
	}
}