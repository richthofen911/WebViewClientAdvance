package net.callofdroidy.webviewcookie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;

public class ActivityMain extends AppCompatActivity {

    TextView tv_resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String cookies = getIntent().getStringExtra("cookies");

        tv_resp = (TextView) findViewById(R.id.tv_TabScreens);

        findViewById(R.id.btn_TabScreens).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ATSendSocketHTTPRequest().execute("GET", "dob.apengage.io", "80", "/aloha/dashboard/braveheart/screens", "Cookie: " + cookies, null, "1");
            }
        });
    }

    //this method will be wrapped in the AsyncTask
    private String sendSocketRequest(String method, String urlHost, int port, String path, String headerExtraData, String bodyData){
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        StringBuilder respLine = new StringBuilder("");
        try{
            socket = new Socket(urlHost, port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(method + " " + path + " HTTP/1.1");  //Method + Path
            writer.println("Host: " + urlHost);   //Host base address
            writer.println("Accept: */*");
            writer.println("User-Agent: curl/7.37.0");
            if(headerExtraData != null){ //check if has extra header data
                Log.e("extra header:", headerExtraData);
                writer.println(headerExtraData);
            }
            writer.println("\r"); // Important, else the server will expect that there's more into the request.
            if(bodyData != null){//check if has extra body data
                Log.e("extra payload data:", bodyData);
                String encodedBodyData = URLEncoder.encode(bodyData, "UTF-8");
                writer.println(encodedBodyData);
            }
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            for (String line; (line = reader.readLine()) != null;) {
                /*
                if (line.isEmpty())
                    break; // Stop when headers are completed. We're not interested in all the HTML.
                */
                respLine.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            if (writer != null) { writer.close(); }
            if (socket != null) try { socket.close(); } catch (IOException logOrIgnore) {}
            return respLine.toString();
        }
    }

    class ATSendSocketHTTPRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String...params){
            return sendSocketRequest(params[0], params[1], Integer.valueOf(params[2]), params[3], params[4], params[5]);
        }
        protected void onPostExecute(String respRaw) {
            tv_resp.setText(respRaw);
        }
    }
}
