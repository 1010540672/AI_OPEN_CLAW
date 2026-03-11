package com.webview.perf;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.utils.FileUtils;

import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class WebViewPerfTransform extends Transform {

    private final Project project;

    public WebViewPerfTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "WebViewPerfTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        outputProvider.deleteAll();

        for (TransformInput input : transformInvocation.getInputs()) {
            // 处理Jar包
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                    jarInput.getFile().getAbsolutePath(),
                    jarInput.getContentTypes(),
                    jarInput.getScopes(),
                    Format.JAR
                );
                FileUtils.copyFile(jarInput.getFile(), dest);
            }

            // 处理目录（主要是class文件）
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(
                    directoryInput.getName(),
                    directoryInput.getContentTypes(),
                    directoryInput.getScopes(),
                    Format.DIRECTORY
                );

                // 字节码插桩
                instrumentClasses(directoryInput.getFile(), dest);

                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }

    private void instrumentClasses(File inputDir, File outputDir) throws IOException {
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            return;
        }

        File[] files = inputDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                instrumentClasses(file, new File(outputDir, file.getName()));
            } else if (file.getName().endsWith(".class")) {
                // 只处理Android和WebView相关的类
                if (shouldInstrument(file)) {
                    byte[] modifiedBytes = instrumentClass(file);
                    if (modifiedBytes != null) {
                        File outputFile = new File(outputDir, file.getName());
                        outputFile.getParentFile().mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            fos.write(modifiedBytes);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldInstrument(File file) {
        String path = file.getAbsolutePath();
        return path.contains("android/webkit") ||
               path.contains("WebView") ||
               path.contains("WebChromeClient") ||
               path.contains("WebViewClient");
    }

    private byte[] instrumentClass(File classFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(classFile)) {
            byte[] bytes = new byte[(int) classFile.length()];
            fis.read(bytes);

            ClassReader classReader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            // 使用自定义的访问器进行插桩
            WebViewPerformanceVisitor visitor = new WebViewPerformanceVisitor(classWriter);
            classNode.accept(visitor);

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
