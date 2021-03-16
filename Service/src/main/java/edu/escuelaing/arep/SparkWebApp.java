package edu.escuelaing.arep;

import static spark.Spark.*;

/**
 * SparkWeb App end-point service
 */
public class SparkWebApp {

    public static void main(String[] args) {
        port(getPort());
        secure("keystores/ecikeystore.p12","daniela","keystores/myTrustStore","alfonso");
        get("/service",(request, response) -> "Service Response");

    }

    /**
     * Retorna el puerto del ambiente, por defecto es 5002
     */
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5002;
    }
}
