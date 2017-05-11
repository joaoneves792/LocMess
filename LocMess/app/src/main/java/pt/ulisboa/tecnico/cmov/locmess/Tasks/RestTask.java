package pt.ulisboa.tecnico.cmov.locmess.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.SSL.MyCustomClientHttpRequestFactory;
import pt.ulisboa.tecnico.cmov.locmess.Tasks.SSL.MyCustomSSLSocketFactory;

/**
 * Created by joao on 3/29/17.
 */

public abstract class RestTask extends AsyncTask<Void, String, String> {

    protected Activity _context;
    protected RestTemplate _rest;
    protected String _url;

    protected Context _appContext;



    /**
     * We need to invoke sslSocket.setEnabledProtocols(new String[] {"SSLv3"});
     * see http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html (Java 8 section)
     */


    @Override
    protected void onPreExecute(){


        MyCustomClientHttpRequestFactory requestFactory = new MyCustomClientHttpRequestFactory();
        _rest = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        _rest.setMessageConverters(messageConverters);
    }

    private KeyStore loadServerCert() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream ins = null;
            if(_appContext != null) {
                ins = _appContext.getResources().openRawResource(_appContext.getResources().getIdentifier("servercert", "raw", _appContext.getPackageName()));
            }else if(_context != null){
                ins = _context.getResources().openRawResource(_context.getResources().getIdentifier("servercert", "raw", _context.getPackageName()));
            }

            if(null == ins){
                throw new IOException("Unable to load server cert!");
            }
            //InputStream caInput = new BufferedInputStream(new FileInputStream("servercert.cer"));
            InputStream caInput = new BufferedInputStream(ins);
            Certificate serverCert;
            try {
                serverCert = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) serverCert).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("server", serverCert);

            return keyStore;
        }catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e){
            Log.e("SSL", "Failed to load sercer cert" + e.getMessage());
            return null;
        }
    }

    private void setURL(Context context){
        //_url = context.getResources().getString(R.string.noEmulatorServerURL);
        _url = context.getResources().getString(R.string.serverURL);
    }

    protected RestTask(Activity appContext){
        super();
        _context = appContext;
        setURL(_context);
    }

    protected RestTask(Context context){
        super();
        _appContext = context;
        setURL(_appContext);
    }

    @Override
    protected abstract String doInBackground(Void... params);

    @Override
    protected abstract void onPostExecute(String result);

}

