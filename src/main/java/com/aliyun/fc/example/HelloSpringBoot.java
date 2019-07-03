package com.aliyun.fc.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyun.fc.runtime.AppLoader;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FcAppLoader;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyun.fc.runtime.HttpRequestHandler;

/**
 * 
 * This class shows how to use Alibaba Cloud Function Compute to run your java SpringBoot webapp
 * The webapp package was upload to OSS
 * 1. In initialize we load the webapp package and init the webapp.
 * 2. In handleRequest we forward the request to the webapp loaded above.
 * 
 * @author santi
 */

public class HelloSpringBoot implements FunctionInitializer, HttpRequestHandler {
    private AppLoader fcAppLoader = new FcAppLoader();
    
    // OSS config
    private String ossEndPoint = System.getenv("OSS_END_POINT");
    private String bucket = System.getenv("OSS_BUCKET");
    private String key = System.getenv("OSS_KEY");
    
    // Request url web path
    // 1. Without custom domain: /2016-08-15/proxy/${YourServiceName}/${YourFunctionName}
    // 2. With custom domain: your mapping settings path
    private String userContextPath = System.getenv("USER_CONTEXT_PATH");
    
    // Webapp package download directory
    private String appDownloadPath = System.getenv("APP_DOWNLOAD_PATH");
    
    // Webapp home directory after inited
    private String appBaseDir = System.getenv("APP_BASE_DIR");
    
    
    @Override
    public void initialize(Context context) throws IOException {
        FunctionComputeLogger fcLogger = context.getLogger();
        
        // Config FcAppLoader
        fcAppLoader.setFCContext(context);
        if (appDownloadPath != null) fcAppLoader.setDownloadPath(appDownloadPath);
        if (appBaseDir != null) fcAppLoader.setBaseDir(appBaseDir);
        
        // Load code from OSS
        fcLogger.info("Begin load code: " + key);
        boolean codeSuccess = fcAppLoader.loadCodeFromOSS(ossEndPoint, bucket, key);
        if (! codeSuccess) {
            throw new IOException("Download code failed");
        }
        fcLogger.info("End load code");
        
        // Init webapp from code
        long timeBegin = System.currentTimeMillis();
        fcLogger.info("Begin load webapp");
        boolean initSuccess = fcAppLoader.initApp(userContextPath, HelloSpringBoot.class.getClassLoader());
        if(! initSuccess) {
            throw new IOException("Init web app failed");
        }
        fcLogger.info("End load webapp, elapsed: " + (System.currentTimeMillis() - timeBegin) + "ms");
    }
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        try {
            fcAppLoader.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
