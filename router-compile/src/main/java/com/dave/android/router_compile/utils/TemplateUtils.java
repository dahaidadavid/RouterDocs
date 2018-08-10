package com.dave.android.router_compile.utils;

import com.dave.android.router_compile.PageServiceMeta;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author rendawei
 * @date 2018/8/6
 */
public class TemplateUtils {

    private static final String TEMPLATE_NAME = "module_api.html";
    private static final String TEMPLATE_KEY = "pages";
    private static final String DEFAULT_PATH = System.getProperty("user.home");
    private static TemplateUtils instance = new TemplateUtils();
    private Configuration mMyCfg = null;

    private TemplateUtils() {
    }

    public static TemplateUtils getInstance() {
        return instance;
    }

    public void init(String outputPath) {
        try {
            mMyCfg = new Configuration();
            mMyCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            mMyCfg.setDefaultEncoding("UTF-8");
            File mFile;
            if (StringUtils.isEmpty(outputPath)) {
                mFile = new File(DEFAULT_PATH, TEMPLATE_NAME);
            } else {
                mFile = new File(outputPath, TEMPLATE_NAME);
            }
            FileUtils.copyInputStreamToFile(getClass().getClassLoader().getResourceAsStream(TEMPLATE_NAME), mFile);
            mMyCfg.setDirectoryForTemplateLoading(mFile.getParentFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Template getTemplate() {
        try {
            return mMyCfg.getTemplate(TEMPLATE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeTemplate(String path, List<PageServiceMeta> pageServiceMetas) {
        OutputStreamWriter mWriter = null;
        try {
            File destFile = new File(path);
            if (!destFile.exists()) {
                destFile.getParentFile().mkdirs();
                destFile.createNewFile();
            }
            mWriter = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
            Map<String, Object> root = new HashMap<>();
            root.put(TEMPLATE_KEY, pageServiceMetas);
            Template mTemplate = getTemplate();
            mTemplate.process(root, mWriter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mWriter != null) {
                    mWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeTemplate(String path, List<PageServiceMeta> pageServiceMetas, Map<String, Object> params) {
        OutputStreamWriter mWriter = null;
        try {
            File destFile = new File(path);
            if (!destFile.exists()) {
                destFile.getParentFile().mkdirs();
                destFile.createNewFile();
            }
            mWriter = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
            Map<String, Object> root = new HashMap<>(params);
            root.put(TEMPLATE_KEY, pageServiceMetas);
            Template mTemplate = getTemplate();
            mTemplate.process(root, mWriter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mWriter != null) {
                    mWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeTemplate(Writer writer, List<PageServiceMeta> pageServiceMetas, Map<String, Object> params) {
        try {
            Map<String, Object> root = new HashMap<>(params);
            root.put(TEMPLATE_KEY, pageServiceMetas);
            Template mTemplate = getTemplate();
            mTemplate.process(root, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
