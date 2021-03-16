package edu.escuelaing.arep;

import com.google.gson.Gson;
import spark.staticfiles.StaticFilesConfiguration;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * WebSpark Secure App 
 */
public class SparkWebApp
{
    public static void main(String[] args) {
        port(getPort());
        Map<String,String> users=new HashMap<>();
        users.put("daniela@gmail.com",Services.hashPass("hola123"));
        secure("keystores/ecikeystore.p12", "daniela", null, null);
        Gson gson = new Gson();
        HttpClient.init();

        before("auth/*", (req, response) ->{
            req.session(true);
            if(req.session().isNew()){
                req.session().attribute("Logged",false);
            }
            boolean auth=req.session().attribute("Logged");
            if(!auth){
                halt(401, "<h1>401 Unauthorized</h1>");
            }});

        before("/login.html",((req, response) ->{
            req.session(true);
            if(req.session().isNew()){
                req.session().attribute("Logged",false);
            }
            boolean auth=req.session().attribute("Logged");
            if(auth){
                response.redirect("auth/index.html");
            }}));

        StaticFilesConfiguration staticHandler = new StaticFilesConfiguration();
        staticHandler.configure("/views");
        before((request, response) ->
                staticHandler.consume(request.raw(), response.raw()));

        get("/",((request, response) -> {
            response.redirect("/login.html");
            return "";
        }));

        get("auth/user",((req, res) -> req.session().attribute("User")));

        post("/login",((request, response) -> {
            request.body();
            request.session(true);
            User user= gson.fromJson(request.body(),User.class);
            if(Services.hashPass(user.getPassword()).equals(users.get(user.getEmail()))){
                request.session().attribute("User",user.getEmail());
                request.session().attribute("Logged",true);
            }
            else{
                return "Usuario o contraseña incorrectos";
            }
            return "";

        }));

        get("/auth/service",(request, response) -> HttpClient.getService());

    }

    /**
     *
     * @return Retorna el puerto del entorno, por defecto es 4567
     */
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; 
    }
}
