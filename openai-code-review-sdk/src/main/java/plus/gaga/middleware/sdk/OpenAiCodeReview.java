package plus.gaga.middleware.sdk;

import com.alibaba.fastjson2.JSON;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gaga.middleware.sdk.domain.sevice.impl.OpenAiCodeReviewService;
import plus.gaga.middleware.sdk.infrastructure.git.GitCommand;
import plus.gaga.middleware.sdk.infrastructure.openai.IOpenAI;

import plus.gaga.middleware.sdk.infrastructure.openai.impl.ChatGLM;
import plus.gaga.middleware.sdk.infrastructure.weixin.WeiXin;
import plus.gaga.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;
import plus.gaga.middleware.sdk.domain.model.Model;
import plus.gaga.middleware.sdk.types.utils.BearerTokenUtils;
import plus.gaga.middleware.sdk.types.utils.WXAccessTokenUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    // 配置配置
    private String weixin_appid = "wx4005d795fa6b64dd";
    private String weixin_secret = "5e6884e0da4f84640ab8d5a30164ccf1";
    private String weixin_touser = "o8ge46ohQKQbjPZRgQhZh9sgghk8";
    private String weixin_template_id = "f0jcyjAB8ZMBRmgR4LfdEapkb2ie-jd2nGUEy05-rTg";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) throws Exception {
        System.out.println("测试执行");

        String token = System.getenv("GITHUB_TOKEN");
        if (null == token || token.isEmpty()) {
            throw new RuntimeException("token is null");
        }


        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /**
         * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
         */
        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );

        IOpenAI openAI = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, weiXin);
        openAiCodeReviewService.exec();

        logger.info("openai-code-review done!");

//        // 1. 代码检出
//        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
//        processBuilder.directory(new File("."));
//
//        Process process = processBuilder.start();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//
//        StringBuilder diffCode = new StringBuilder();
//        while ((line = reader.readLine()) != null) {
//            diffCode.append(line);
//        }
//
//        int exitCode = process.waitFor();
//        System.out.println("Exited with code:" + exitCode);
//
//        System.out.println("diff code：" + diffCode.toString());
//
//        // 2. chatglm 代码评审
//        String log = codeReview(diffCode.toString());
//        System.out.println("code review：" + log);
//
//        // 3. 评审日志写入文件
//        String logUrl = writeLog(token,log);
//        System.out.println("writeLog" + logUrl);
//
//        // 4. 消息通知
//        System.out.println("pushMessage：" + logUrl);
//        pushMessage(logUrl);
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

//    private static void pushMessage(String logUrl) {
//        String accessToken = WXAccessTokenUtils.getAccessToken();
//        System.out.println(accessToken);
//
//        TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO();
//        templateMessageDTO.put("project", "ZZY-OPENAI");
//        templateMessageDTO.put("review", logUrl);
//        templateMessageDTO.setUrl(logUrl);
//        templateMessageDTO.setTemplate_id("1ezucvi0Kog8IoT-4Ym1qFK7p_F6SW9uzad3xTOopZw");
//
//        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);
//        sendPostRequest(url, JSON.toJSONString(templateMessageDTO));
//    }

//    private static void sendPostRequest(String urlString, String jsonBody) {
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json; utf-8");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setDoOutput(true);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
//                os.write(input, 0, input.length);
//            }
//
//            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
//                String response = scanner.useDelimiter("\\A").next();
//                System.out.println(response);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private static String codeReview(String diffCode) throws Exception {
//
//        String apiKeySecret = "c9eed2f14fb746ad8c768ca5c946867e.AzCEdFmHQMT4mN7z";
//        String token = BearerTokenUtils.getToken(apiKeySecret);
//
//        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Authorization", "Bearer " + token);
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        connection.setDoOutput(true);
//
////        String jsonInpuString = "{"
////                + "\"model\":\"glm-4-flash\","
////                + "\"messages\": ["
////                + "    {"
////                + "        \"role\": \"user\","
////                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: " + diffCode + "\""
////                + "    }"
////                + "]"
////                + "}";
//
//        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
//        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
//        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
//            private static final long serialVersionUID = -7988151926241837899L;
//
//            {
//                add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
//                add(new ChatCompletionRequest.Prompt("user", diffCode));
//            }
//        });
//
//        try (OutputStream os = connection.getOutputStream()) {
//            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
//            os.write(input);
//        }
//
//        int responseCode = connection.getResponseCode();
//        System.out.println(responseCode);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//
//        StringBuilder content = new StringBuilder();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//
//        in.close();
//        connection.disconnect();
//
//        ChatCompletionSyncResponse response = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
//        return response.getChoices().get(0).getMessage().getContent();
//    }
//    private static String writeLog(String token, String log) throws Exception {
//        Git git = Git.cloneRepository()
//                .setURI("https://github.com/ZZY976/openai-code-review-log")
//                .setDirectory(new File("repo"))
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
//                .call();
//
//        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//        File dateFolder = new File("repo/" + dateFolderName);
//        if (!dateFolder.exists()) {
//            dateFolder.mkdirs();
//        }
//
//        String fileName = generateRandomString(12) + ".md";
//        File newFile = new File(dateFolder, fileName);
//        try (FileWriter writer = new FileWriter(newFile)) {
//            writer.write(log);
//        }
//
//        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
//        git.commit().setMessage("Add new file via GitHub Actions").call();
//        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
//
//        System.out.println("Changes have been pushed to the repository.");
//
//        return "https://github.com/ZZY976/openai-code-review-log/blob/master/" + dateFolderName + "/" + fileName;
//    }
//
//    private static String generateRandomString(int length) {
//        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder(length);
//        for (int i = 0; i < length; i++) {
//            sb.append(characters.charAt(random.nextInt(characters.length())));
//        }
//        return sb.toString();
//    }

}
