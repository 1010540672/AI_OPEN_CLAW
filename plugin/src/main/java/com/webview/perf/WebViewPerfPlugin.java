package com.webview.perf;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class WebViewPerfPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("webviewPerf", WebViewPerfExtension.class);
        project.getDependencies().add("implementation", "androidx.webkit:webkit:1.8.0");

        // Register the transform
        def android = project.extensions.findByType(com.android.build.gradle.AppExtension.class);
        if (android != null) {
            android.registerTransform(new WebViewPerfTransform(project));
        }
    }
}

public static class WebViewPerfExtension {
    boolean enableTrace = true;
    boolean enableVisualization = true;
    String packageName = "";
}
