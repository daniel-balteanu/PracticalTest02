package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {


    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        String rezultat_remote;

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word / minLength)");
            String cuvant = bufferedReader.readLine();
            String minLength = bufferedReader.readLine();
            Log.d(Constants.TAG, "Received from client: " + cuvant + " " + minLength);



            HttpClient httpClient = new DefaultHttpClient();
//            String server_name = Constants.WEB_SERVICE_ADDRESS
//                    + "?" + "anagram" + "=" + cuvant + "&" + "minLetters" + "=" + minLength;
//            Log.d(Constants.TAG, "Server address is: " + server_name);
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS
                    + "?" + "anagram" + "=" + cuvant + "&" + "minLetters" + "=" + Integer.parseInt(minLength));
//            HttpGet httpGet = new HttpGet("http://services.aonaware.com/CountCheatService/CountCheatService.asmx/LetterSolutionsMin?anagram=programming&minLetters=7");
//            HttpGet httpGet = new HttpGet(server_name);
//            HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
//            List<NameValuePair> params = new ArrayList<>();
//            params.add(new BasicNameValuePair("anagram", cuvant));
//            params.add(new BasicNameValuePair("minLetters", minLength));
//            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            httpPost.setEntity(urlEncodedFormEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpGet, responseHandler);
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] Just executed");
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }
            Log.d(Constants.TAG, pageSourceCode);

            Document document = Jsoup.parse(pageSourceCode);
//            Element element = document.child(0);
//            Elements elements = document.getElementsByTag("string");
//////            Log.d(Constants.TAG, "size of elements is " + elements.le)
////            for (Element script: elements) {
////                String scriptData = script.data();
////                Log.d(Constants.TAG, scriptData);
////            }
            String pagina = document.toString();
            String result = "";
            Log.d(Constants.TAG, pagina);
            Log.d(Constants.TAG, "------------");

            Scanner scanner = new Scanner(document.toString());
//            for (int i = 0 ; i < 10; i++) {
//                scanner.nextLine();
//            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Log.d(Constants.TAG, line);
                Log.d(Constants.TAG, "------------");
                if (!line.contains("<")) {
                    result += line + " ";
                }
            }
            scanner.close();

            printWriter.write(result);
            printWriter.flush();



        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
//            if (Constants.DEBUG) {
//                ioException.printStackTrace();
//            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
//                    if (Constants.DEBUG) {
//                        ioException.printStackTrace();
//                    }
                }
            }
        }
    }
}
