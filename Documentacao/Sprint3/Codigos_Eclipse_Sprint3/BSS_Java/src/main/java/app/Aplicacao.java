package app;

import static spark.Spark.*;
import java.io.IOException;
import service.SiteService;
import service.UsuarioService;
import service.RecomendarService;


public class Aplicacao {
	private static SiteService siteService = new SiteService();
	private static UsuarioService usuarioService = new UsuarioService();
	private static RecomendarService recomendarService = new RecomendarService();
	
    public static void main(String[] args) throws IOException {
    	port(6789);

        post("/site", (request, response) -> siteService.add(request, response));

        get("/site/:url", (request, response) -> siteService.get(request, response));

        get("/site/update/:url", (request, response) -> siteService.update(request, response));

        get("/site/delete/:url", (request, response) -> siteService.remove(request, response));

        get("/site", (request, response) -> siteService.getAll(request, response));
        
        
        post("/usuario/create", (request, response) -> usuarioService.add(request, response));

        get("/usuario/:nome", (request, response) -> usuarioService.get(request, response));

        get("/usuario/update/:nome", (request, response) -> usuarioService.update(request, response));

        get("/usuario/delete/:nome", (request, response) -> usuarioService.remove(request, response));

        get("/usuario", (request, response) -> usuarioService.getAll(request, response));
        
        
        post("/recomendar", (request, response) -> recomendarService.add(request, response));

        get("/recomendar/:sites_url", (request, response) -> recomendarService.get(request, response));

        get("/recomendar/update/:sites_url", (request, response) -> recomendarService.update(request, response));

        get("/recomendar/delete/:sites_url", (request, response) -> recomendarService.remove(request, response));

        get("/recomendar", (request, response) -> recomendarService.getAll(request, response));
        
        get("/recomendar/count/:sites_url", (request, response) -> recomendarService.count(request, response));
    }
}