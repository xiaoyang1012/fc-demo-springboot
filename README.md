# fc-demo-springboot
Alibaba Cloud Function Compute (FC) java http trigger demo for loading SpringBoot

Install:
    1. Upload your SpringBoot application war to aliyun OSS
    2. Fill the OSS info to `template.yml`
    3. deploy:
        - Linux/Mac OS: `./deploy.sh`
        - Windows: 
            - mvn clean package
            - fun deploy

Test:
    curl the address return from deploy, such as:
    ```bash
        curl https://{account_id}.{region}.fc.aliyuncs.com/2016-08-15/proxy/demo-springboot-service/demo-springboot/
    ```