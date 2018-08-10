package com.dave.android.router_compile.processor;

import static com.dave.android.router_compile.utils.Consts.ACTIVITY;
import static com.dave.android.router_compile.utils.Consts.FRAGMENT;
import static com.dave.android.router_compile.utils.Consts.FRAGMENT_V4;
import static com.dave.android.router_compile.utils.Consts.SERVICE;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dave.android.router_annotation.PageFiled;
import com.dave.android.router_annotation.PageService;
import com.dave.android.router_compile.PageFieldMeta;
import com.dave.android.router_compile.PageServiceMeta;
import com.dave.android.router_compile.utils.Consts;
import com.dave.android.router_compile.utils.Logger;
import com.dave.android.router_compile.utils.TemplateUtils;
import com.google.auto.service.AutoService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author rendawei
 * @date 2018/5/15
 */
@AutoService(Processor.class)
@SupportedOptions({Consts.KEY_MODULE_NAME, Consts.KEY_OUT_PATH, Consts.KEY_TEMPLATE_PATH, Consts.KEY_DOC_TYPE})
public class PageDocProcessor extends AbstractProcessor {

    private static final String DOC_TYPE_JSON = "json";
    private static final String DOC_TYPE_HTML = "html";
    private static final String DOC_TYPE_ROUTER = "router";
    private static final String DOC_NAME_FORMAT_HTML = "router-map-%s.html";
    private static final String DOC_NAME_FORMAT_JSON = "router-map-%s.json";

    private Logger mLogger;
    private Types types;
    private Elements elements;
    private Filer mFiler;

    private List<PageServiceMeta> mPageServiceMetaList = new ArrayList<>();

    /**
     * 最终的Html输出全路径
     */
    private String mOutPutHtmlPath;
    /**
     * 模板的解压临时路径
     */
    private String mTemplatePath;
    /**
     * 模块名称
     */
    private String mModuleName;
    /**
     * 生成的Doc类型，如果需要使用Arouter格式为router|json|html,第一位为强制使用Arouter的注解，默认是自定义注解
     */
    private String mDocType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mLogger = new Logger(processingEnvironment.getMessager());
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();

        //read the config
        Map<String, String> options = processingEnv.getOptions();
        if (!MapUtils.isEmpty(options)) {
            mModuleName = options.get(Consts.KEY_MODULE_NAME);
            mOutPutHtmlPath = options.get(Consts.KEY_OUT_PATH);
            mTemplatePath = options.get(Consts.KEY_TEMPLATE_PATH);
            mDocType = options.get(Consts.KEY_DOC_TYPE);
        }

        mLogger.info("Init ==> KEY_OUT_PATH::" + mOutPutHtmlPath
                + " KEY_TEMPLATE_PATH::" + mTemplatePath
                + " KEY_MODULE_NAME::" + mModuleName
                + " KEY_DOC_TYPE::" + mDocType);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        //Close the doc function
        if (StringUtils.isEmpty(mDocType)) {
            return false;
        }
        if (!roundEnvironment.processingOver()) {
            mLogger.info("===================Process Begin======================");
            if (CollectionUtils.isEmpty(annotations)) {
                return false;
            }
            //Find the Page,default use the custom annotation
            if (mDocType.toLowerCase().contains(DOC_TYPE_ROUTER)) {
                Set<? extends Element> mPageServiceSets = roundEnvironment.getElementsAnnotatedWith(Route.class);
                findARouterAnnotationPage(mPageServiceSets);
            } else {
                Set<? extends Element> mPageServiceSets = roundEnvironment.getElementsAnnotatedWith(PageService.class);
                findCustomAnnotationPage(mPageServiceSets);
            }
        } else {
            mLogger.info("===================Pages:"+mPageServiceMetaList.size());
            if(!mPageServiceMetaList.isEmpty()){
                if (mDocType.toLowerCase().contains(DOC_TYPE_JSON) && mDocType.toLowerCase().contains(DOC_TYPE_HTML)) {
                    if (StringUtils.isEmpty(mOutPutHtmlPath)) {
                        writeDefaultHtml();
                        writeDefaultJson();
                    } else {
                        writeHtmlByPath();
                        writeJsonByPath();
                    }
                } else if (mDocType.toLowerCase().contains(DOC_TYPE_JSON)) {
                    if (StringUtils.isEmpty(mOutPutHtmlPath)) {
                        writeDefaultJson();
                    } else {
                        writeJsonByPath();
                    }
                } else {
                    if (StringUtils.isEmpty(mOutPutHtmlPath)) {
                        writeDefaultHtml();
                    } else {
                        writeHtmlByPath();
                    }
                }
            }
            mLogger.info("===================Process End========================");
        }
        return false;
    }

    private void writeHtmlByPath() {
        try {
            TemplateUtils.getInstance().init(mTemplatePath);
            Map<String, Object> params = new HashMap<>(1);
            params.put("moduleName", mModuleName);
            File outFile = new File(mOutPutHtmlPath,String.format(Locale.CHINA, DOC_NAME_FORMAT_HTML, mModuleName));
            TemplateUtils.getInstance().writeTemplate(new FileWriter(outFile),mPageServiceMetaList, params);
        } catch (Exception e) {
            mLogger.error("Create doc writer failed, because " + e.getMessage());
        }
    }

    private void writeDefaultHtml() {
        try {
            FileObject mHtmlResource = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, "com.dave.android.router_compile.docs",
                    String.format(Locale.CHINA, DOC_NAME_FORMAT_HTML, mModuleName));
            TemplateUtils.getInstance().init(mTemplatePath);
            Map<String, Object> params = new HashMap<>(1);
            params.put("moduleName", mModuleName);
            TemplateUtils.getInstance().writeTemplate(mHtmlResource.openWriter(), mPageServiceMetaList, params);
        } catch (IOException e) {
            mLogger.error("Create doc writer failed, because " + e.getMessage());
        }
    }

    private void writeJsonByPath() {
        OutputStreamWriter mOutputStreamWriter = null;
        try {
            mOutputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(new File(mOutPutHtmlPath, String.format(Locale.CHINA, DOC_NAME_FORMAT_JSON, mModuleName))));
            mOutputStreamWriter.write(JSONObject.toJSONString(mPageServiceMetaList, SerializerFeature.PrettyFormat));
            mOutputStreamWriter.flush();
        } catch (Exception e) {
            mLogger.error("Create doc writer failed, because " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(mOutputStreamWriter);
        }
    }

    private void writeDefaultJson() {
        try {
            FileObject mJsonResource = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, "com.dave.android.router_compile.docs",
                    String.format(Locale.CHINA, DOC_NAME_FORMAT_JSON, mModuleName));
            Writer mWriter = mJsonResource.openWriter();
            mWriter.write(JSONObject.toJSONString(mPageServiceMetaList, SerializerFeature.PrettyFormat));
            mWriter.flush();
            mWriter.close();
        } catch (IOException e) {
            mLogger.error("Create doc writer failed, because " + e.getMessage());
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    /**
     * Find the custom annotation
     *
     * @param pageElements the element to be find
     */
    private void findCustomAnnotationPage(Set<? extends Element> pageElements) {
        if (CollectionUtils.isEmpty(pageElements)) {
            return;
        }
        mPageServiceMetaList.clear();
        TypeMirror type_Activity = elements.getTypeElement(ACTIVITY).asType();
        TypeMirror type_Service = elements.getTypeElement(SERVICE).asType();
        TypeMirror fragmentTm = elements.getTypeElement(FRAGMENT).asType();
        TypeMirror fragmentTmV4 = elements.getTypeElement(FRAGMENT_V4).asType();

        for (Element element : pageElements) {
            mLogger.info("Find a element:" + element.getSimpleName());
            TypeMirror tm = element.asType();
            PageService mPageService = element.getAnnotation(PageService.class);
            if (mPageService == null) {
                continue;
            }
            //builder pagerService
            PageServiceMeta mPageServiceMeta = new PageServiceMeta();
            mPageServiceMeta.clazzName = element.asType().toString();
            mPageServiceMeta.name = mPageService.name();
            mPageServiceMeta.desc = mPageService.desc();
            mPageServiceMeta.path = mPageService.path();
            mPageServiceMeta.version = mPageService.version();

            for (Element field : element.getEnclosedElements()) {
                PageFieldMeta mPageFieldMeta;
                if (field.getKind().isField() && field.getAnnotation(PageFiled.class) != null) {
                    PageFiled paramConfig = field.getAnnotation(PageFiled.class);
                    mPageFieldMeta = new PageFieldMeta();
                    mPageFieldMeta.clazzName = field.asType().toString();
                    mPageFieldMeta.desc = paramConfig.desc();
                    mPageFieldMeta.name = paramConfig.name();
                    mPageFieldMeta.required = paramConfig.required();
                    mPageFieldMeta.version = paramConfig.version();
                    mPageServiceMeta.params.add(mPageFieldMeta);
                }
            }

            if (types.isSubtype(tm, type_Activity)) {
                mPageServiceMeta.type = ACTIVITY;
            } else if (types.isSubtype(tm, type_Service)) {
                mPageServiceMeta.type = SERVICE;
            } else if (types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                mPageServiceMeta.type = FRAGMENT;
            } else {
                mPageServiceMeta.type = "Other";
            }

            //Add PageServiceMeta
            mPageServiceMetaList.add(mPageServiceMeta);
        }
    }

    /**
     * Find the ARouter annotation
     *
     * @param pageElements the element to be find
     */
    private void findARouterAnnotationPage(Set<? extends Element> pageElements) {
        if (CollectionUtils.isEmpty(pageElements)) {
            return;
        }
        TypeMirror typeActivity = elements.getTypeElement(ACTIVITY).asType();
        TypeMirror typeService = elements.getTypeElement(SERVICE).asType();
        TypeMirror fragmentTm = elements.getTypeElement(FRAGMENT).asType();
        TypeMirror fragmentTmV4 = elements.getTypeElement(FRAGMENT_V4).asType();

        for (Element element : pageElements) {
            TypeMirror tm = element.asType();
            Route mPageService = element.getAnnotation(Route.class);
            if (mPageService == null) {
                continue;
            }
            mLogger.info("Find a element:" + element.getSimpleName());
            String descFormat = "GROUP:%s EXTRA:%d PRIORITY:%d";
            //builder pagerService
            PageServiceMeta mPageServiceMeta = new PageServiceMeta();
            mPageServiceMeta.clazzName = element.asType().toString();
            mPageServiceMeta.name = mPageService.name();
            mPageServiceMeta.desc = String.format(Locale.CHINA, descFormat, mPageService.group(), mPageService.extras(), mPageService.priority());
            mPageServiceMeta.path = mPageService.path();
            mPageServiceMeta.version = "non";

            for (Element field : element.getEnclosedElements()) {
                PageFieldMeta mPageFieldMeta;
                if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null) {
                    Autowired paramConfig = field.getAnnotation(Autowired.class);
                    mPageFieldMeta = new PageFieldMeta();
                    mPageFieldMeta.clazzName = field.asType().toString();
                    mPageFieldMeta.desc = paramConfig.desc();
                    mPageFieldMeta.name = StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() : paramConfig.name();
                    mPageFieldMeta.required = paramConfig.required();
                    mPageFieldMeta.version = "non";
                    mPageServiceMeta.params.add(mPageFieldMeta);
                }
            }

            if (types.isSubtype(tm, typeActivity)) {
                mPageServiceMeta.type = ACTIVITY;
            } else if (types.isSubtype(tm, typeService)) {
                mPageServiceMeta.type = SERVICE;
            } else if (types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                mPageServiceMeta.type = FRAGMENT;
            } else {
                mPageServiceMeta.type = "Other";
            }

            //Add PageServiceMeta
            mPageServiceMetaList.add(mPageServiceMeta);
        }
    }
}
