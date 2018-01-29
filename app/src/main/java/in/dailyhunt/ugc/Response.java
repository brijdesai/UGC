package in.dailyhunt.ugc;

/**
 * Created by brij on 29/1/18.
 */

public class Response {
    private int responseCode;
    private String response;

    public Response(int responseCode,String response) {
        this.responseCode = responseCode;
        this.response=response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getResponseCode() {

        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
